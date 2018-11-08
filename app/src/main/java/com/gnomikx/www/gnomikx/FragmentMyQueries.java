package com.gnomikx.www.gnomikx;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.gnomikx.www.gnomikx.Data.QueryDetails;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.gnomikx.www.gnomikx.ViewHolders.QueryViewHolder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Class to act as the logic for Fragment MyQueries
 */

public class FragmentMyQueries extends Fragment {

    private FirestoreRecyclerAdapter adapter;
    private FirebaseUser user;
    private TextView noQueryTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_queries, container, false);
        RecyclerView myQueryRecyclerView = rootView.findViewById(R.id.my_query_recyclerview);

        noQueryTextView = rootView.findViewById(R.id.my_query_no_data);

        FirebaseHandler handler = new FirebaseHandler();
        user = handler.getFirebaseUser();

        Query query;
        if(user != null) {
            query = FirebaseFirestore.getInstance()
                    .collection("Queries")
                    .whereEqualTo("userID", user.getUid());

            FirestoreRecyclerOptions<QueryDetails> options = new FirestoreRecyclerOptions.Builder<QueryDetails>()
                    .setQuery(query, QueryDetails.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<QueryDetails, QueryViewHolder>(options) {
                @Override
                public void onBindViewHolder(QueryViewHolder holder, int position, final QueryDetails model) {
                    holder.itemView.setTag(model.getDocumentId());
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    String displayedString = (model.getBody().length() > 100) ? model.getBody().substring(0, 98) + getString(R.string.ellipsis)
                           : model.getBody();
                    holder.bind(dateFormat.format(model.getTimestamp()) , model.getTitle(), displayedString);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("Date", dateFormat.format(model.getTimestamp()));
                            bundle.putString("Title", model.getTitle());
                            bundle.putString("Body", model.getBody());
                            bundle.putString("Response", model.getResponse());

                            ViewQueryFragment viewQueryFragment = new ViewQueryFragment();
                            viewQueryFragment.setArguments(bundle);

                            MainActivity activity = (MainActivity) FragmentMyQueries.this.getActivity();
                            FragmentManager fragmentManager = activity.getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            //showing fragment
                            fragmentTransaction.replace(R.id.fragment_container, viewQueryFragment, MainActivity.VIEW_QUERY_FRAGMENT);
                            fragmentTransaction.addToBackStack(MainActivity.VIEW_QUERY_FRAGMENT).commit();
                        }
                    });
                }

                @Override
                public void onDataChanged() {
                    if(getItemCount()==0){
                        noQueryTextView.setVisibility(View.VISIBLE);
                    }else {
                        noQueryTextView.setVisibility(View.GONE);
                    }
                    super.onDataChanged();
                }

                @Override
                public void onError(@NonNull FirebaseFirestoreException e) {
                    super.onError(e);
                }

                @Override
                public QueryViewHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext())
                            .inflate(R.layout.query_list_item, group, false);

                    return new QueryViewHolder(view);
                }
            };

            myQueryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            myQueryRecyclerView.setAdapter(adapter);

        } else {
            Toast.makeText(getContext(), "You need to be signed in to see this", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (user != null && adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(user != null && adapter != null) {
            adapter.stopListening();
        }
    }
}
