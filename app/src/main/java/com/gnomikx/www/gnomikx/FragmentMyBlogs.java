package com.gnomikx.www.gnomikx;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gnomikx.www.gnomikx.Adapters.BlogAdapter;
import com.gnomikx.www.gnomikx.Data.Blog;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.gnomikx.www.gnomikx.ViewHolders.BlogViewHolder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

/**
 * Class to act as the logic for the Fragment MyBlogs
 */

public class FragmentMyBlogs extends Fragment{

    private FirestoreRecyclerAdapter adapter;
    private TextView noBlogText;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_blogs, container, false);

        noBlogText = rootView.findViewById(R.id.my_blog_no_data);
        RecyclerView myBlogRecyclerView = rootView.findViewById(R.id.my_blog_recyclerview);

        final FirebaseHandler handler = new FirebaseHandler();
        user = handler.getFirebaseUser();

        Query query;
        if(user != null) {
            query = handler.getBlogCollectionReference()
                    .whereEqualTo("userId", user.getUid())
                    .orderBy("timestamp", Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<Blog> options = new FirestoreRecyclerOptions.Builder<Blog>()
                    .setQuery(query, Blog.class)
                    .build();

            adapter = new BlogAdapter(options, this, noBlogText);

            myBlogRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            myBlogRecyclerView.setAdapter(adapter);
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
