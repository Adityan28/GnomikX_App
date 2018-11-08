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
import com.gnomikx.www.gnomikx.Data.FavoriteBlog;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.gnomikx.www.gnomikx.ViewHolders.BlogViewHolder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Class for Favorite blogs fragment - to manage favorite blogs
 */

public class FragmentFavoriteBlogs extends Fragment {

    private FirebaseHandler firebaseHandler;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite_blogs, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.favorite_blogs_recycler_view);
        TextView noDataTextView = rootView.findViewById(R.id.my_fav_blogs_no_data);
        firebaseHandler = new FirebaseHandler();
        user = firebaseHandler.getFirebaseUser();

        if(user != null) {
            final Query query = firebaseHandler.getFavoritesCollectionReference()
                    .whereEqualTo("userId", user.getUid());

            runRecyclerView(query, recyclerView, noDataTextView);
        }
        else {
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

    private void runRecyclerView(Query query, RecyclerView recyclerView, final TextView noDataTextView) {
        FirestoreRecyclerOptions<FavoriteBlog> options = new FirestoreRecyclerOptions.Builder<FavoriteBlog>()
                .setQuery(query, FavoriteBlog.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<FavoriteBlog, BlogViewHolder>(options) {
            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blog_list_item, parent, false);

                return new BlogViewHolder(view);
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
            protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull final FavoriteBlog model) {
                final Blog blog = model.getFavBlog();
                holder.itemView.setTag(blog.getDocumentId());

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                holder.bind(blog.getHeadline(), blog.getTrailText(), dateFormat.format(blog.getTimestamp()));

                if(blog.getImageName() != null && !blog.getImageName().equals("")) {
                    StorageReference storageReference = firebaseHandler.getBlogImagesStorageReference().child(blog.getImageName());
                    ImageView imageView = holder.getHeadlineImage();
                    GlideApp.with(FragmentFavoriteBlogs.this)
                            .load(storageReference)
                            .into(imageView);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("BlogAdapter", "Click event received");

                        MainActivity activity = (MainActivity) FragmentFavoriteBlogs.this.getActivity();
                        assert activity != null;
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        DisplayBlogContentsFragment displayBlogContentFragment = new DisplayBlogContentsFragment();

                        //passing necessary parameters to Fragment
                        Bundle bundle = new Bundle();
                        bundle.putString("Heading", blog.getHeadline());
                        bundle.putString("Body", blog.getBody());
                        bundle.putString("Trail Text", blog.getTrailText());
                        bundle.putString("UserId", blog.getUserId());
                        bundle.putBoolean("Diabetes Tag", blog.isDiabetesTag());
                        bundle.putBoolean("Obesity Tag", blog.isObesityTag());
                        bundle.putBoolean("General Health Tag", blog.isGeneralHealthTag());
                        bundle.putString("Image Name", blog.getImageName());
                        bundle.putString("Document Id", blog.getDocumentId());
                        bundle.putSerializable("Timestamp", blog.getTimestamp());
                        displayBlogContentFragment.setArguments(bundle);

                        //showing fragment
                        fragmentTransaction.replace(R.id.fragment_container, displayBlogContentFragment);
                        fragmentTransaction.addToBackStack(null).commit();
                    }
                });
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
