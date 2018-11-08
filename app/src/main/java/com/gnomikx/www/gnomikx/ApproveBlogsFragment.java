package com.gnomikx.www.gnomikx;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gnomikx.www.gnomikx.Adapters.BlogAdapter;
import com.gnomikx.www.gnomikx.Data.Blog;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;


public class ApproveBlogsFragment extends Fragment {

    private FirestoreRecyclerAdapter adapter;

    public ApproveBlogsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_approve_blogs, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.approve_blog_recycler_view);
        final TextView noDataTextView = rootView.findViewById(R.id.approve_blogs_no_data);

        FirebaseHandler firebaseHandler = new FirebaseHandler();
        FirebaseUser user = firebaseHandler.getFirebaseUser();
        
        if(user != null) {
            if(user.isEmailVerified()) {

                Query query = firebaseHandler.getBlogCollectionReference()
                        .whereEqualTo("approved", false)
                        .orderBy("timestamp", Query.Direction.DESCENDING);

                final FirestoreRecyclerOptions<Blog> options = new FirestoreRecyclerOptions.Builder<Blog>()
                        .setQuery(query, Blog.class)
                        .build();

                adapter = new BlogAdapter(options, ApproveBlogsFragment.this, noDataTextView);

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
