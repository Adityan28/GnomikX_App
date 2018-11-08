package com.gnomikx.www.gnomikx.Adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gnomikx.www.gnomikx.Data.Blog;
import com.gnomikx.www.gnomikx.DisplayBlogContentsFragment;
import com.gnomikx.www.gnomikx.GlideApp;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.gnomikx.www.gnomikx.MainActivity;
import com.gnomikx.www.gnomikx.R;
import com.gnomikx.www.gnomikx.ViewHolders.BlogViewHolder;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Class to act as adapter for the RecyclerView of the BlogFragment
 */

public class BlogAdapter extends FirestoreRecyclerAdapter<Blog, BlogViewHolder> {

    private FirebaseHandler firebaseHandler;
    private Fragment fragment;
    private TextView noDatatextview;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options - sets the options for FirestoreRecyclerAdapter
     */
    public BlogAdapter(@NonNull FirestoreRecyclerOptions<Blog> options, Fragment fragment, TextView noDatatextview) {
        super(options);
        firebaseHandler = new FirebaseHandler();
        this.fragment = fragment;
        this.noDatatextview = noDatatextview;
    }

    @Override
    protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull final Blog model) {
        holder.itemView.setTag(model.getDocumentId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        holder.bind(model.getHeadline(), model.getTrailText(), dateFormat.format(model.getTimestamp()));
        noDatatextview = holder.itemView.findViewById(R.id.my_blog_no_data);

        if(model.getImageName() != null && !model.getImageName().equals("")) {
            StorageReference storageReference = firebaseHandler.getBlogImagesStorageReference().child(model.getImageName());
            ImageView imageView = holder.getHeadlineImage();
            GlideApp.with(fragment)
                    .load(storageReference)
                    .into(imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("BlogAdapter", "Click event received");

                MainActivity activity = (MainActivity) fragment.getActivity();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                DisplayBlogContentsFragment displayBlogContentFragment = new DisplayBlogContentsFragment();

                //passing necessary parameters to Fragment
                Bundle bundle = new Bundle();
                bundle.putString("Heading", model.getHeadline());
                bundle.putString("Body", model.getBody());
                bundle.putString("Trail Text", model.getTrailText());
                bundle.putString("UserId", model.getUserId());
                bundle.putBoolean("Diabetes Tag", model.isDiabetesTag());
                bundle.putBoolean("Obesity Tag", model.isObesityTag());
                bundle.putBoolean("General Health Tag", model.isGeneralHealthTag());
                bundle.putString("Image Name", model.getImageName());
                bundle.putString("Document Id", model.getDocumentId());
                bundle.putBoolean("Approved", model.isApproved());
                bundle.putSerializable("Timestamp", model.getTimestamp());
                displayBlogContentFragment.setArguments(bundle);

                //showing fragment
                fragmentTransaction.replace(R.id.fragment_container, displayBlogContentFragment, MainActivity.DISPLAY_BLOG_TAG);
                fragmentTransaction.addToBackStack(MainActivity.DISPLAY_BLOG_TAG).commit();
            }
        });
    }

    @Override
    public void onDataChanged() {
        if(noDatatextview != null) {
            if (getItemCount() == 0) {
                noDatatextview.setVisibility(View.VISIBLE);
            } else {
                noDatatextview.setVisibility(View.GONE);
            }
        }
        super.onDataChanged();
    }

    @Override
    public BlogViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.blog_list_item, group, false);

        return new BlogViewHolder(view);
    }
}
