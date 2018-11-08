package com.gnomikx.www.gnomikx;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gnomikx.www.gnomikx.Data.UserDetail;
import com.gnomikx.www.gnomikx.Data.UserReports;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.gnomikx.www.gnomikx.ViewHolders.AllUsersViewHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllUsersFragment extends Fragment {

    private static final int RC_GET_REPORT = 789;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseHandler handler;
    private UserDetail userDetail;

    public AllUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_all_users, container, false);
        
        final TextView noDataTextView = rootView.findViewById(R.id.my_report_no_data);

        handler = new FirebaseHandler();
        FirebaseUser user = handler.getFirebaseUser();
        
        if(user != null) {
            if(user.isEmailVerified()) {
                CollectionReference userDetailsCollectionsRef = handler.getUserDetailsCollectionReference();

                Query query = userDetailsCollectionsRef.orderBy("userName", Query.Direction.ASCENDING);

                RecyclerView recyclerView = rootView.findViewById(R.id.upload_report_recycler_view);

                FirestoreRecyclerOptions<UserDetail> options = new FirestoreRecyclerOptions.Builder<UserDetail>()
                        .setQuery(query, UserDetail.class)
                        .build();

                adapter = new FirestoreRecyclerAdapter<UserDetail, AllUsersViewHolder>(options) {
                    @Override
                    public AllUsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.users_list_item, parent, false);

                        return new AllUsersViewHolder(view);
                    }

                    @Override
                    public void onDataChanged() {
                        if(noDataTextView != null) {
                            if (getItemCount() == 0) {
                                noDataTextView.setVisibility(View.VISIBLE);
                            } else {
                                noDataTextView.setVisibility(View.GONE);
                            }
                        }
                        super.onDataChanged();
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull AllUsersViewHolder holder, int position, @NonNull final UserDetail model) {
                        holder.itemView.setTag(model.getDocumentId());
                        holder.bind(model.getUserName(), model.getUserEmailID(), model.getPhoneNumber());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userDetail = model;
                                DisplayUserDetailsFragment displayUserDetailsFragment = new DisplayUserDetailsFragment();

                                Bundle bundle = new Bundle();
                                bundle.putString("User name", model.getUserName());
                                bundle.putString("Date of birth", model.getDateOfBirth());
                                bundle.putString("Email", model.getUserEmailID());
                                bundle.putString("Phone", model.getPhoneNumber());
                                bundle.putInt("Gender", model.getGender());
                                bundle.putString("Role", model.getRole());
                                displayUserDetailsFragment.setArguments(bundle);

                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, displayUserDetailsFragment,
                                        MainActivity.DISPLAY_USER_FRAGMENT);
                                fragmentTransaction.addToBackStack(MainActivity.DISPLAY_USER_FRAGMENT);
                                fragmentTransaction.commit();
                            }
                        });
                    }
                };

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            } else {
                noDataTextView.setVisibility(View.VISIBLE);
                noDataTextView.setText(R.string.email_not_verified_text);
                Toast.makeText(getContext(), getText(R.string.email_not_verified_text), Toast.LENGTH_SHORT).show();
            }
        } else {
            noDataTextView.setVisibility(View.VISIBLE);
            noDataTextView.setText(R.string.user_not_signed_in_text);
            Toast.makeText(getContext(), getString(R.string.user_not_signed_in_text), Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
