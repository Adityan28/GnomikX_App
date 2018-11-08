package com.gnomikx.www.gnomikx;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gnomikx.www.gnomikx.Adapters.BlogAdapter;
import com.gnomikx.www.gnomikx.Data.Blog;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.google.firebase.firestore.Query;

/**
 * Fragment class for displaying list of blogs
 */
public class BlogFragment extends Fragment {

    private FirestoreRecyclerAdapter adapter;
    private FirebaseHandler firebaseHandler;
    private Query query;

    public BlogFragment() {
        // Required empty public constructor
    }

    private static final String diabetesTag = "diabetesTag";
    private static final String obesityTag = "obesityTag";
    private static final String generalHealthTag = "generalHealthTag";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflateView = inflater.inflate(R.layout.fragment_blog, container, false);

        final RecyclerView recyclerView = inflateView.findViewById(R.id.blog_recycler_view);
        final TextView noDataTextView = inflateView.findViewById(R.id.blogs_no_data);

        firebaseHandler = new FirebaseHandler();

        final CheckBox diabetesCheckBox = inflateView.findViewById(R.id.diabetes_filter);
        final CheckBox obesityCheckBox = inflateView.findViewById(R.id.obesity_filter);
        final CheckBox gHeatlhCheckBox = inflateView.findViewById(R.id.general_health_filter);

        query = firebaseHandler.getBlogCollectionReference()
                .whereEqualTo("approved", true)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onStop();
                Query insideQuery = firebaseHandler.getQueryCollectionRefrence();
                Log.i("CheckedChanged", "check change detected");
                if(!diabetesCheckBox.isChecked() && !obesityCheckBox.isChecked() && !gHeatlhCheckBox.isChecked()) {
                    //nothing selected
                    insideQuery = firebaseHandler.getBlogCollectionReference();
                }
                else if(diabetesCheckBox.isChecked() && !obesityCheckBox.isChecked() && !gHeatlhCheckBox.isChecked()) {
                    //only diabetes selected
                    insideQuery = firebaseHandler.getBlogCollectionReference().whereEqualTo(diabetesTag, true);
                }

                 else if(!diabetesCheckBox.isChecked() && obesityCheckBox.isChecked() && !gHeatlhCheckBox.isChecked()) {
                    //only obesity selected
                    insideQuery = firebaseHandler.getBlogCollectionReference().whereEqualTo(obesityTag, true);
                }

                else if(!diabetesCheckBox.isChecked() && !obesityCheckBox.isChecked() && gHeatlhCheckBox.isChecked()) {
                    //only general health selected
                    insideQuery = firebaseHandler.getBlogCollectionReference().whereEqualTo(generalHealthTag, true);
                }

                else if(diabetesCheckBox.isChecked() && obesityCheckBox.isChecked() && !gHeatlhCheckBox.isChecked()) {
                    //diabetes and obesity selected
                    insideQuery = firebaseHandler.getBlogCollectionReference().
                            whereEqualTo(diabetesTag, true).whereEqualTo(obesityTag, true);
                }
                else if(diabetesCheckBox.isChecked() && !obesityCheckBox.isChecked() && gHeatlhCheckBox.isChecked()) {
                    //diabetes and general health selected
                    insideQuery = firebaseHandler.getBlogCollectionReference()
                            .whereEqualTo(diabetesTag,true).whereEqualTo(generalHealthTag, true);
                }
                else if(!diabetesCheckBox.isChecked() && obesityCheckBox.isChecked() && gHeatlhCheckBox.isChecked()) {
                    //obesity and general health selected
                    insideQuery = firebaseHandler.getBlogCollectionReference()
                            .whereEqualTo(obesityTag, true).whereEqualTo(generalHealthTag, true);
                }
                else if(!diabetesCheckBox.isChecked() && !obesityCheckBox.isChecked() && gHeatlhCheckBox.isChecked()) {
                    //all filters selected
                    insideQuery = firebaseHandler.getBlogCollectionReference()
                            .whereEqualTo(diabetesTag, true)
                            .whereEqualTo(obesityTag, true)
                            .whereEqualTo(generalHealthTag, true);
                }

                query = insideQuery.whereEqualTo("approved", true).orderBy("timestamp", Query.Direction.DESCENDING);

                final FirestoreRecyclerOptions<Blog> options = new FirestoreRecyclerOptions.Builder<Blog>()
                        .setQuery(query, Blog.class)
                        .build();

                adapter = new BlogAdapter(options, BlogFragment.this, noDataTextView);
                recyclerView.setAdapter(adapter);
                onStart();
            }
        };

        diabetesCheckBox.setOnCheckedChangeListener(checkedChangeListener);
        obesityCheckBox.setOnCheckedChangeListener(checkedChangeListener);
        gHeatlhCheckBox.setOnCheckedChangeListener(checkedChangeListener);

        final FirestoreRecyclerOptions<Blog> options = new FirestoreRecyclerOptions.Builder<Blog>()
                .setQuery(query, Blog.class)
                .build();

        adapter = new BlogAdapter(options, BlogFragment.this, noDataTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return inflateView;
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
