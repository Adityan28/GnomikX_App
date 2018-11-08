package com.gnomikx.www.gnomikx;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.gnomikx.www.gnomikx.ActivityWidgetHandler.MakeVisible;
import com.gnomikx.www.gnomikx.Data.UserDetail;
import com.gnomikx.www.gnomikx.Handlers.DateOfBirthPickerHandler;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

/**
 * Class for SignUpFragment - create a GnomikX account of the user
 */

public class SignUpFragment extends DialogFragment {

    private Button signUpButton;
    private EditText phoneNumberEditText;
    private EditText dateOfBirthEditText;
    private String role;
    private boolean completed;
    private FirebaseHandler handler;
    private int gender;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        completed = false;
        rootView.setBackgroundColor(Color.WHITE);
        handler = new FirebaseHandler();

        MainActivity activity = (MainActivity) getActivity();
        activity.getSupportActionBar().hide();

        Toolbar toolbar = rootView.findViewById(R.id.sign_up_dialog_toolbar);
        toolbar.setTitle(R.string.sign_up_text);

        signUpButton = rootView.findViewById(R.id.sign_up_button);
        phoneNumberEditText = rootView.findViewById(R.id.phone_number_editText);
        progressBar = rootView.findViewById(R.id.sign_up_fragment_progress_bar);
        dateOfBirthEditText = rootView.findViewById(R.id.date_of_birth_editText);
        new DateOfBirthPickerHandler(dateOfBirthEditText, getContext());
        final RadioGroup roleRadio = rootView.findViewById(R.id.role_radio_group);
        final RadioGroup genderRadio = rootView.findViewById(R.id.gender_radio_group);

        genderRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                TextView noGenderSelectedError = rootView.findViewById(R.id.no_gender_selected_error_text);
                if(checkedId == R.id.radio_gender_male) {
                    gender = MainActivity.GENDER_MALE;
                    noGenderSelectedError.setVisibility(View.GONE);
                } else if (checkedId == R.id.radio_gender_female) {
                    noGenderSelectedError.setVisibility(View.GONE);
                    gender = MainActivity.GENDER_FEMALE;
                } else if(checkedId == R.id.radio_gender_other) {
                    noGenderSelectedError.setVisibility(View.GONE);
                    gender = MainActivity.GENDER_OTHER;
                }
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;
                if(phoneNumberEditText.getText() == null || phoneNumberEditText.getText().toString().equals("") ||
                        phoneNumberEditText.getText().toString().isEmpty()){
                    phoneNumberEditText.setError(getString(R.string.no_phone_number_entered));
                    error = true;
                }
                if(dateOfBirthEditText.getText() == null || dateOfBirthEditText.getText().toString().isEmpty() ||
                        dateOfBirthEditText.getText().toString().equals("")) {
                    dateOfBirthEditText.setError(getString(R.string.no_date_of_birth_selected));
                    error = true;
                }
                if(genderRadio.getCheckedRadioButtonId() == -1) {
                    //no buttons are checked
                    TextView noGenderSelectedError = rootView.findViewById(R.id.no_gender_selected_error_text);
                    noGenderSelectedError.setVisibility(View.VISIBLE);
                    error = true;
                }
                if(!error){
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseUser user = handler.getFirebaseUser();
                    role = (roleRadio.getCheckedRadioButtonId() == R.id.patient_radio_group) ? MainActivity.ROLE_PATIENT :
                            ((roleRadio.getCheckedRadioButtonId() == R.id.doctor_radio_group) ? MainActivity.ROLE_DOCTOR : MainActivity.ROLE_ADMIN);
                    String phoneNumber = phoneNumberEditText.getText().toString();
                    String dateOfBirth = dateOfBirthEditText.getText().toString();
                    final UserDetail userDetail = new UserDetail(user.getDisplayName(), user.getEmail(), phoneNumber, dateOfBirth, gender, role);
                    final MainActivity activity = (MainActivity) getActivity();
                    activity.setUserDetail(userDetail);
                    CollectionReference userDetailsCollectionReference = handler.getUserDetailsCollectionReference();
                    userDetailsCollectionReference.document(user.getUid()).set(userDetail)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.GONE);
                                    completed = true;
                                    showEmailVerificationAlertDialog();
                                    activity.showMenuAndNavFields(userDetail);
                                    dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(!completed) {
            Log.i("Not complete", "Delete account");
            AuthUI authUI = handler.getAuthUI();
            authUI.delete(getContext());
        }
        super.onDismiss(dialog);
    }

    private void showEmailVerificationAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.email_verification_required)
                .setMessage(R.string.email_verification_message)
                .setPositiveButton(R.string.check_email_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                        try {
                            startActivity(Intent.createChooser(intent, getString(R.string.choose_email_client)));
                        }
                        catch(ActivityNotFoundException e){
                            Toast.makeText(getContext(), R.string.R_string_no_email_application_found, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.verify_later, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //simply dismiss the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDetach() {
        MakeVisible makeVisible = (MakeVisible) getActivity();
        if (makeVisible != null) {
            makeVisible.makeVisible();
        }
        super.onDetach();
    }
}

