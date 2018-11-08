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

import com.gnomikx.www.gnomikx.Data.RegisterPatients;
import com.gnomikx.www.gnomikx.Data.UserDetail;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Fragment class for Registering Patients
 */

public class FragmentRegisterPatients extends Fragment{

    private EditText mRegisterNumber;
    private EditText mRegisterName;
    private Button mRegisterButton;
    FirebaseHandler firebaseHandler;
    private MainActivity activity;
    private ProgressBar progressBar;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register_patients, container, false);

        mRegisterNumber = rootView.findViewById(R.id.register_number_edittext);
        mRegisterName = rootView.findViewById(R.id.register_name_edittext);
        mRegisterButton = rootView.findViewById(R.id.register_button);
        progressBar = rootView.findViewById(R.id.register_patient_progress_bar);

        firebaseHandler = new FirebaseHandler();
        user = firebaseHandler.getFirebaseUser();
        if(user != null) {
            activity = (MainActivity)getContext();
            assert activity != null;
            UserDetail userDetail = activity.userDetail;

            if(userDetail == null) {
                //getUserDetails
                getUserDetail();
            } else {
                executeFunction(userDetail);
            }
        }

        else {
            //user is not signed in, initiate login flow
            SignInDialog signInDialog = new SignInDialog();
            signInDialog.show(getChildFragmentManager(), "sign in");
        }

        return rootView;
    }

    private void executeFunction(final UserDetail userDetail) {
        if(userDetail.getRole().equals(getString(R.string.doctor))) {
            //only doctor is allowed to perform anything here

            mRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(user.isEmailVerified()) {
                        int patientNumber;
                        String patientName;
                        boolean error = false;

                        if (mRegisterNumber.getText().toString().trim().isEmpty()) {
                            mRegisterNumber.setError(getString(R.string.no_register_patient_number));
                            error = true;
                        } else if (mRegisterButton.getText().toString().trim().length() > 6) {
                            mRegisterNumber.setError(getString(R.string.regn_number_too_long));
                            error = true;
                        }

                        if (!error) {
                            progressBar.setVisibility(View.VISIBLE);
                            patientName = mRegisterName.getText().toString().trim();
                            patientNumber = Integer.parseInt(mRegisterNumber.getText().toString().trim());
                            FirebaseHandler firebaseHandler = new FirebaseHandler();

                            CollectionReference registerCollectionReference = firebaseHandler.getRegisterPatientCollectionReference();
                            RegisterPatients registerPatients = new RegisterPatients();
                            registerPatients.setPatientNumber(patientNumber);
                            registerPatients.setPatientName(patientName);
                            registerPatients.setDoctorDetails(userDetail);
                            registerCollectionReference.add(registerPatients).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getContext(), "Patient Registered", Toast.LENGTH_SHORT).show();
                                    mRegisterNumber.setText("");
                                    mRegisterName.setText("");
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
        } else {
            //not a doctor
            Toast.makeText(getContext(), R.string.patient_accessed_register_patients_page_warning, Toast.LENGTH_SHORT).show();
            mRegisterButton.setEnabled(false);
            mRegisterName.setEnabled(false);
            mRegisterNumber.setEnabled(false);
        }
    }

    private void getUserDetail() {
        DocumentReference userDetailsDocRef = firebaseHandler.getUserDetailsDocumentRef();
        userDetailsDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    UserDetail userDetail = documentSnapshot.toObject(UserDetail.class);
                    activity.setUserDetail(userDetail);
                    //execute function once user details are available
                    executeFunction(userDetail);
                } else {
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
