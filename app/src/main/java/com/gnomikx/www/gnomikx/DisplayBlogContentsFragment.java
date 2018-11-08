package com.gnomikx.www.gnomikx;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gnomikx.www.gnomikx.Data.Blog;
import com.gnomikx.www.gnomikx.Data.FavoriteBlog;
import com.gnomikx.www.gnomikx.Data.UserDetail;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Class to display entire blogs
 */

public class DisplayBlogContentsFragment extends Fragment {

    private boolean favorite;
    private String favDocId;
    private static final String TAG = "DispBlogContents";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_display_blog_content, container, false);

        final FirebaseHandler handler = new FirebaseHandler();
        final FirebaseUser user = handler.getFirebaseUser();

        final MainActivity activity = (MainActivity) getActivity();

        UserDetail userDetail = activity.getUserDetail();

        final Blog blog = new Blog();

        //grabbing details of selected document
        Bundle bundle = getArguments();
        assert bundle != null;
        blog.setHeadline(bundle.getString("Heading"));
        blog.setBody(bundle.getString("Body"));
        blog.setTrailText(bundle.getString("Trail Text"));
        blog.setUserId(bundle.getString("UserId"));
        blog.setDiabetesTag(bundle.getBoolean("Diabetes Tag"));
        blog.setObesityTag(bundle.getBoolean("Obesity Tag"));
        blog.setGeneralHealthTag(bundle.getBoolean("General Health Tag"));
        blog.setImageName(bundle.getString("Image Name"));
        blog.setDocumentId(bundle.getString("Document Id"));
        blog.setTimestamp((Date)bundle.getSerializable("Timestamp"));
        blog.setApproved(bundle.getBoolean("Approved"));
        favDocId = "";

        final Button favoriteBlogButton = rootView.findViewById(R.id.favorite_blog_button);
        final Button approveButton = rootView.findViewById(R.id.approve_button);

        if(userDetail != null) {
            if(userDetail.getRole().equals(MainActivity.ROLE_ADMIN)) {
                favoriteBlogButton.setVisibility(View.GONE);
                approveButton.setVisibility(View.VISIBLE);
            } else {
                approveButton.setVisibility(View.GONE);
                favoriteBlogButton.setVisibility(View.VISIBLE);
            }
        }

        approveButtonSettings(approveButton, blog);


        favorite = false;

        //check if this document is a favorite
        if(user != null) {
            handler.getFavoritesCollectionReference().whereEqualTo("favBlog.documentId", blog.getDocumentId())
                    .whereEqualTo("userId", user.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            List<DocumentSnapshot> snapshots = documentSnapshots.getDocuments();
                            if(snapshots.size() > 0) {
                                //this document is a favorite, make appropriate change on the button
                                favDocId = snapshots.get(0).getId();
                                favoriteBlogButton.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_favorite_filled, 0);
                                favorite = true;
                            }
                        }
                    });
        }

        //setting blog parameters
        TextView headingText = rootView.findViewById(R.id.display_blog_headline);
        headingText.setText(blog.getHeadline());

        TextView bodyText = rootView.findViewById(R.id.display_blog_body);
        bodyText.setText(blog.getBody());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        TextView dateText = rootView.findViewById(R.id.display_blog_date);
        dateText.setText(dateFormat.format(blog.getTimestamp()));

        ImageView imageView = rootView.findViewById(R.id.details_blog_image);
        if(blog.getImageName() != null && !blog.getImageName().equals("")) {
            //image has been uploaded with blog
            Log.i("Image name", blog.getImageName());
            StorageReference storageReference = handler.getBlogImagesStorageReference().child(blog.getImageName());
            GlideApp.with(this)
                    .load(storageReference)
                    .into(imageView);
        } else {
            //image has not been uploaded, setting gradient_blog_images as placeholder image
            imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.gradient_blog_images));
        }

        favoriteBlogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Button thisButton = (Button) v;
                if(favorite) {
                    thisButton.setEnabled(false);
                    //document has been removed from being favorite
                    DocumentReference documentReference = handler.getFavoritesCollectionReference().document(favDocId);
                    Log.i(TAG, documentReference.toString());
                    thisButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_border, 0);
                    documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            thisButton.setEnabled(true);
                        }
                    });
                    favorite = false;
                    favDocId = "";
                } else {
                    thisButton.setEnabled(false);
                    //document has been selected as favorite
                    if(user != null) {
                        FavoriteBlog favoriteBlog = new FavoriteBlog(user.getUid(), blog, blog.getTimestamp());
                        handler.getFavoritesCollectionReference().add(favoriteBlog)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        favDocId = documentReference.getId();
                                        thisButton.setEnabled(true);
                                    }
                                });
                        thisButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_favorite_filled, 0);
                        favorite = true;
                    }
                    else {
                        Toast.makeText(getContext(), "You must be signed in to set document as your favorite", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DocumentReference blogRef = handler.getBlogCollectionReference().document(blog.getDocumentId());
                Button b = (Button) v;
                b.setEnabled(false);
                Log.i(TAG, "approve button clicked");
                if(!blog.isApproved()) {
                    blogRef.update("approved", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "Blog approved");
                            blog.setApproved(true);
                            Toast.makeText(activity, R.string.blog_approved, Toast.LENGTH_SHORT).show();
                            approveButtonSettings((Button) v, blog);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "Blog could not be approved \n" + e.getMessage());
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    blogRef.update("approved", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            blog.setApproved(false);
                            Log.i(TAG, "Approval of blog has been removed");
                            approveButtonSettings((Button) v, blog);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity, "Blog could not be approved \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return rootView;
    }

    /**
     * method to update the looks of the approve button
     * @param approveButton - contains a reference to the button
     * @param blog - contains the details of the blog
     */
    private void approveButtonSettings(Button approveButton, Blog blog) {
        if(blog.isApproved()) {
            approveButton.setText(getString(R.string.approved));
            approveButton.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_box_white_24dp,0);
        } else {
            approveButton.setText(getString(R.string.approve));
            approveButton.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_box_outline_blank_white_24dp,0);
        }
        approveButton.setEnabled(true);
    }
}
