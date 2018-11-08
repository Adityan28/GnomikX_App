package com.gnomikx.www.gnomikx;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gnomikx.www.gnomikx.Data.UserDetail;
import com.gnomikx.www.gnomikx.Data.UserReports;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayUserDetailsFragment extends Fragment {

    private FirebaseHandler handler;
    private UserDetail userDetail;
    private static final int RC_UPLOAD_REPORT = 777;

    public DisplayUserDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        handler = new FirebaseHandler();

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_display_user_details, container, false);

        Bundle bundle = getArguments();
        userDetail = new UserDetail(bundle.getString("User name"), bundle.getString("Email"), bundle.getString("Phone"),
                bundle.getString("Date of birth"), bundle.getInt("Gender"), bundle.getString("Role"));

        //set display params
        TextView userName = rootView.findViewById(R.id.display_user_name);
        userName.setText(getString(R.string.display_username, userDetail.getUserName()));

        TextView userEmail = rootView.findViewById(R.id.display_user_email);
        userEmail.setText(getString(R.string.display_useremail, userDetail.getUserEmailID()));

        TextView userPhone = rootView.findViewById(R.id.display_user_phone);
        userPhone.setText(getString(R.string.display_userphone, userDetail.getPhoneNumber()));

        TextView userDOB = rootView.findViewById(R.id.display_user_dob);
        userDOB.setText(getString(R.string.display_userDOB, userDetail.getDateOfBirth()));

        TextView userGender = rootView.findViewById(R.id.display_user_gender);
        switch(userDetail.getGender()) {
            case MainActivity.GENDER_MALE:
                userGender.setText(getString(R.string.display_usergender, getString(R.string.male)));
                break;
            case MainActivity.GENDER_FEMALE:
                userGender.setText(getString(R.string.display_usergender, getString(R.string.female)));
                break;
            case MainActivity.GENDER_OTHER:
                userGender.setText(getString(R.string.display_usergender, getString(R.string.gender_other)));
                break;
            default: userGender.setText(getString(R.string.unknown));
        }

        Button callUser = rootView.findViewById(R.id.call_user_button);
        callUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + userDetail.getPhoneNumber()));
                startActivity(intent);
            }
        });

        Button sendEmail = rootView.findViewById(R.id.send_email_to_user_button);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, userDetail.getUserEmailID());
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_salutation, userDetail.getUserName()));
                startActivity(Intent.createChooser(intent, getString(R.string.send_email_chooser_text)));
            }
        });

        Button uploadReport = rootView.findViewById(R.id.upload_report_button);
        uploadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (android.os.Build.VERSION.SDK_INT >= 19) {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                else {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                }
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");  //PDF
                try {
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.get_pdf_chooser_title)), RC_UPLOAD_REPORT);
                } catch (ActivityNotFoundException e) {
                    MainActivity activity = (MainActivity) getActivity();
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent returnIntent) {
        if(requestCode == RC_UPLOAD_REPORT) {
            // If the selection didn't work
            if (resultCode != RESULT_OK) {
                // Exit without doing anything else
            } else {
                // Get the file's content URI from the incoming Intent
                Uri returnUri = returnIntent.getData();
                assert returnUri != null;
                final String fileName = returnUri.getLastPathSegment();
                StorageReference storageReference = handler.getReportsStorageReference().child(fileName);
                storageReference.putFile(returnUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.i("File added", fileName);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });

                CollectionReference reportsCollectionReference = handler.getReportsCollectionReference();
                UserReports userReports = new UserReports(fileName, userDetail);
                reportsCollectionReference.add(userReports).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Report uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
