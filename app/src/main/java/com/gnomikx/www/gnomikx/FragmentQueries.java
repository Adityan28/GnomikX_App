package com.gnomikx.www.gnomikx;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gnomikx.www.gnomikx.Data.QueryDetails;
import com.gnomikx.www.gnomikx.Data.UserDetail;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Fragment class for placing queries
 */

public class FragmentQueries extends Fragment {

    private static final int QUERY_DESCRIPTION_MAX_LENGTH = 500;
    private EditText mQueryTitle;
    private EditText mQueryBody;
    private FirebaseHandler firebaseHandler;
    private String title;
    private String body;
    private FirebaseUser user;
    private MainActivity activity;
    private static final int QUERY_TITLE_MAX_LENGTH = 65;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_queries, container, false);

        firebaseHandler = new FirebaseHandler();
        user = firebaseHandler.getFirebaseUser();

        activity = (MainActivity) getContext();
        mQueryTitle = rootView.findViewById(R.id.query_title_edittext);
        mQueryBody = rootView.findViewById(R.id.query_body_edittext);
        Button submitQuery = rootView.findViewById(R.id.submit_query_button);
        progressBar = rootView.findViewById(R.id.ask_query_progress_bar);

        submitQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.isEmailVerified()) {
                    title = "";
                    body = "";
                    boolean error = false;

                    if (mQueryTitle.getText().toString().trim().isEmpty() || mQueryTitle.getText().toString().trim().equals("")) {
                        mQueryTitle.setError(getString(R.string.no_query_title_entered));
                        error = true;
                    } else if (mQueryTitle.getText().toString().length() > QUERY_TITLE_MAX_LENGTH) {
                        mQueryTitle.setError(getString(R.string.title_too_long_error));
                        error = true;
                    } else {
                        title = mQueryTitle.getText().toString().trim();
                    }

                    if (mQueryBody.getText().toString().trim().isEmpty() || mQueryBody.getText().toString().trim().equals("")) {
                        mQueryBody.setError(getString(R.string.no_query_body_entered));
                        error = true;
                    } else if (mQueryBody.getText().toString().length() > QUERY_DESCRIPTION_MAX_LENGTH) {
                        mQueryBody.setError(getString(R.string.description_too_long_error));
                        error = true;
                    } else {
                        body = mQueryBody.getText().toString().trim();
                    }

                    if (!error) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (user != null) {
                            UserDetail userDetail = activity.getUserDetail();
                            if (userDetail != null) {
                                executeFunction(userDetail);
                            } else {
                                getUserDetail();
                            }
                        } else {
                            //user not signed in, start login flow
                            SignInDialog signInDialog = new SignInDialog();
                            signInDialog.show(getChildFragmentManager(), "sign in");
                            progressBar.setVisibility(View.GONE);
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

        return rootView;
    }

    private void executeFunction(UserDetail userDetail) {
        CollectionReference queryCollectionReference = firebaseHandler.getQueryCollectionRefrence();
        QueryDetails queryDetails = new QueryDetails();
        queryDetails.setUsername(userDetail.getUserName());
        queryDetails.setUserID(user.getUid());
        queryDetails.setTitle(title);
        queryDetails.setBody(body);
        queryDetails.setResponse(null);

        queryCollectionReference.add(queryDetails).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                documentReference.update("documentId", documentReference.getId());
                Toast.makeText(getContext(), "Query submitted", Toast.LENGTH_SHORT).show();
                mQueryTitle.setText("");
                mQueryBody.setText("");
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getUserDetail() {
        DocumentReference userDetailsDocumentReference = firebaseHandler.getUserDetailsDocumentRef();
        userDetailsDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    UserDetail userDetail = documentSnapshot.toObject(UserDetail.class);
                    activity.setUserDetail(userDetail);
                    executeFunction(userDetail);
                }
                else {
                    Toast.makeText(activity, "Some error occurred. Please try to post your query again later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        user = firebaseHandler.getFirebaseUser();
        super.onResume();
    }
}
