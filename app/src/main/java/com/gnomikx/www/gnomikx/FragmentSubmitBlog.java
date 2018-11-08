package com.gnomikx.www.gnomikx;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gnomikx.www.gnomikx.Data.Blog;
import com.gnomikx.www.gnomikx.Data.UserDetail;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment class for Submitting Blog
 */

public class FragmentSubmitBlog extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 111;

    private View rootView;
    private Uri imageUri;
    private String imageName;
    private FirebaseHandler firebaseHandler;
    private ProgressBar progressBar;
    private Button removeImage;
    private FirebaseUser user;
    private UserDetail userDetail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_submit_blog, container, false);

        imageUri = null;
        final MainActivity activity = (MainActivity) getActivity();
        userDetail = activity.getUserDetail();

        if(userDetail == null) {
            DocumentReference documentReference = firebaseHandler.getUserDetailsDocumentRef();
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userDetail = documentSnapshot.toObject(UserDetail.class);
                    activity.setUserDetail(userDetail);
                }
            });
        }

        firebaseHandler = new FirebaseHandler();
        user = firebaseHandler.getFirebaseUser();

        final EditText headingEditText = rootView.findViewById(R.id.submit_blog_heading);
        final EditText bodyEditText = rootView.findViewById(R.id.submit_blog_body);

        progressBar = rootView.findViewById(R.id.submit_blog_progress_bar);

        final AppCompatCheckBox diabetesTagCheckBox = rootView.findViewById(R.id.diabetes_tag);
        final AppCompatCheckBox obesitysTagCheckBox = rootView.findViewById(R.id.obesity_tag);
        final AppCompatCheckBox generalHealthTagCheckBox = rootView.findViewById(R.id.general_health_tag);

        removeImage = rootView.findViewById(R.id.remove_image_button);

        final CompoundButton.OnCheckedChangeListener checkedChangedListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(diabetesTagCheckBox.isChecked() || obesitysTagCheckBox.isChecked()
                        || generalHealthTagCheckBox.isChecked()) {
                    TextView noTagsSelectedText = rootView.findViewById(R.id.no_tags_selected_textView);
                    noTagsSelectedText.setVisibility(View.GONE);
                }
            }
        };

        Button uploadImageButton = rootView.findViewById(R.id.upload_image_button);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                //TODO: Check if permission for READ_EXTERNAL_STORAGE has been granted, ask if not granted
                //get image from gallery
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        Button submitBlogButton = rootView.findViewById(R.id.submit_blog_submit_button);
        submitBlogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.isEmailVerified()) {
                    String heading = "", body = "";
                    StringBuilder trailText = new StringBuilder("");

                    //error checking (similar method as BMIFragment)
                    boolean error = false;

                    //checking if heading has been entered
                    if (headingEditText.getText() == null || headingEditText.getText().toString().equals("")) {
                        headingEditText.setError(getString(R.string.no_heading_entered));
                        error = true;
                    } else {
                        heading = headingEditText.getText().toString();
                    }

                    //checking if body text has been entered
                    if (bodyEditText.getText() == null || bodyEditText.getText().toString().equals("")) {
                        bodyEditText.setError(getString(R.string.no_body_entered));
                        error = true;
                    } else {
                        body = bodyEditText.getText().toString();
                        String arr[] = body.split("\\s+");
                        int length = (arr.length < 20) ? arr.length : 20;
                        for (int i = 0; i < length; i++) { //taking first 20 words as trailText for now
                            if (i < length - 1)
                                trailText.append(arr[i]).append(" ");
                            else {
                                trailText.append(arr[i]).append(getString(R.string.ellipsis)); //find proper library or method to add the ellipsis ("...")
                            }
                        }
                    }

                    //checking if tags have been selected
                    if (!diabetesTagCheckBox.isChecked() && !obesitysTagCheckBox.isChecked()
                            && !generalHealthTagCheckBox.isChecked()) {
                        error = true;
                        TextView noTagsErrorText = rootView.findViewById(R.id.no_tags_selected_textView);
                        noTagsErrorText.setVisibility(View.VISIBLE);
                    }

                    //hiding error text if no any tag has been checked
                    diabetesTagCheckBox.setOnCheckedChangeListener(checkedChangedListener);
                    obesitysTagCheckBox.setOnCheckedChangeListener(checkedChangedListener);
                    generalHealthTagCheckBox.setOnCheckedChangeListener(checkedChangedListener);

                    if (!error) { //no errors were encountered, submit blog to database
                        progressBar.setVisibility(View.VISIBLE);

                        if (user != null) {
                            //user is authenticated, proceed further
                            imageName = "";

                            //upload image, if any to firebase storage
                            if (imageUri != null) {
                                imageName = imageUri.getLastPathSegment();
                                StorageReference storageReference = firebaseHandler.getBlogImagesStorageReference().
                                        child(imageName);
                                storageReference.putFile(imageUri)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Log.i("FragmentSubmitBlog", "Image uploaded successfully");
                                            }
                                        });
                            }

                            //checking tags to add
                            boolean diabetesTag = false, obesityTag = false, generalHealthTag = false;
                            if (diabetesTagCheckBox.isChecked()) {
                                diabetesTag = true;
                            }

                            if (obesitysTagCheckBox.isChecked()) {
                                obesityTag = true;
                            }

                            if (generalHealthTagCheckBox.isChecked()) {
                                generalHealthTag = true;
                            }


                            //creating object of the data to be uploaded
                            final CollectionReference blogCollectionReference = firebaseHandler.getBlogCollectionReference();
                            final Blog blog = new Blog();
                            blog.setUserId(user.getUid());
                            blog.setHeadline(heading);
                            blog.setTrailText(trailText.toString());
                            blog.setBody(body);
                            blog.setImageName(imageName);
                            blog.setDiabetesTag(diabetesTag);
                            blog.setObesityTag(obesityTag);
                            blog.setGeneralHealthTag(generalHealthTag);
                            if(userDetail.getRole().equals(MainActivity.ROLE_ADMIN)) {
                                blog.setApproved(true);
                            } else {
                                blog.setApproved(false);
                            }

                            //upload blog contents into FirebaseFirestore
                            blogCollectionReference.add(blog).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    blog.setDocumentId(documentReference.getId());
                                    documentReference.update("documentId", documentReference.getId());
                                    //clearing the submitted content from UI
                                    Toast.makeText(getContext(), R.string.blog_submitted_message, Toast.LENGTH_SHORT).show();
                                    headingEditText.setText("");
                                    bodyEditText.setText("");
                                    progressBar.setVisibility(View.GONE);
                                    if (imageUri != null) {
                                        ImageView imageView = rootView.findViewById(R.id.submit_blog_image_view);
                                        imageView.setImageBitmap(null);
                                    }
                                    imageName = "";
                                    imageUri = null;
                                    diabetesTagCheckBox.setChecked(false);
                                    obesitysTagCheckBox.setChecked(false);
                                    generalHealthTagCheckBox.setChecked(false);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    if(progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            //not authenticated, start login flow
                            SignInDialog signInDialog = new SignInDialog();
                            signInDialog.show(getChildFragmentManager(), "sign in");
                        }
                    }
                } else {
                    final ProgressBar dialogProgressBar = new ProgressBar(getContext());
                    dialogProgressBar.setId(R.id.dialog_box_progress_bar);
                    dialogProgressBar.setVisibility(View.GONE);
                    final MainActivity activity = (MainActivity) getContext();
                    //email is not verified
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.email_verification_required)
                            .setMessage(R.string.email_verification_required_for_action)
                            .setIcon(R.drawable.ic_warning)
                            .setView(dialogProgressBar)
                            .setPositiveButton(R.string.send_verification_email, null)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogProgressBar.setVisibility(View.VISIBLE);
                            user.sendEmailVerification().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(activity, R.string.email_sending_failure_text, Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(activity, R.string.verification_email_sent, Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });

        removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //removing displayed image
                ImageView imageView = rootView.findViewById(R.id.submit_blog_image_view);
                imageView.setImageBitmap(null);
                imageUri = null;
                v.setVisibility(View.GONE);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //image has been picked from gallery by user
            Uri uri = data.getData();

            try {
                //getting the received image as a Bitmap and setting it to the ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                imageUri = uri;
                ImageView imageView = rootView.findViewById(R.id.submit_blog_image_view);
                imageView.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
                removeImage.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        user = firebaseHandler.getFirebaseUser();
        super.onResume();
    }
}
