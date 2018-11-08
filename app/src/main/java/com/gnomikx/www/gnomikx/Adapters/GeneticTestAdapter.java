package com.gnomikx.www.gnomikx.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gnomikx.www.gnomikx.Data.RegisterTest;
import com.gnomikx.www.gnomikx.Data.UserDetail;
import com.gnomikx.www.gnomikx.Data.GeneticTest;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.gnomikx.www.gnomikx.Handlers.TestDatePickerHandler;
import com.gnomikx.www.gnomikx.Handlers.TestTimePickerHandler;
import com.gnomikx.www.gnomikx.MainActivity;
import com.gnomikx.www.gnomikx.R;
import com.gnomikx.www.gnomikx.SignInDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

/**
 * Class to act as the adapter for RecyclerView of FragmentGeneticTest
 */

public class GeneticTestAdapter extends ArrayAdapter<GeneticTest>{

    private static final int RC_SIGN_IN = 123;
    private ProgressBar progressBar;

    public GeneticTestAdapter(@NonNull Context context, ArrayList<GeneticTest> arrayList, ProgressBar progressBar) {
        super(context,0,arrayList);
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final GeneticTest geneticTest = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.genetic_test_items,parent,false);
        }

        ImageView testImageView = convertView.findViewById(R.id.genetic_test_imageview);
        Button registerButton = convertView.findViewById(R.id.genetic_test_register_button);

        assert geneticTest != null;
        testImageView.setImageResource(geneticTest.getmTestImageID());
        registerButton.setText(geneticTest.getmRegisterButtonText());

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseHandler firebaseHandler = new FirebaseHandler();
                final CollectionReference registerTestCollectionReference = firebaseHandler.getRegisterGeneticTestCollectionReference();
                final RegisterTest registerTest = new RegisterTest();
                switch (position){
                    case 0:
                        registerTest.setTestName("Diabetes");
                        break;
                    case 1:
                        registerTest.setTestName("Hypertension");
                        break;
                    case 2:
                        registerTest.setTestName("Obesity");
                        break;
                }

                final FirebaseUser user = firebaseHandler.getFirebaseUser();
                if(user != null) {
                    if(user.isEmailVerified()) {
                        //user is signed in
                        final MainActivity activity = (MainActivity) getContext();
                        UserDetail userDetail = activity.getUserDetail();
                        if (userDetail != null) {
                            //user details have already been fetched, grab them and register for test
                            registerTest.setUserDetails(userDetail);
                            showDateTimeDialog(registerTest, registerTestCollectionReference);
                        } else {
                            //fetch user details from database, set it to main activity and register
                            DocumentReference userDetailsDocumentReference = firebaseHandler.getUserDetailsDocumentRef();
                            userDetailsDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    UserDetail userDetail = documentSnapshot.toObject(UserDetail.class);
                                    activity.setUserDetail(userDetail);

                                    //display alert dialog to ask for date and time of test
                                    showDateTimeDialog(registerTest, registerTestCollectionReference);
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
                } else {
                    //user is not signed in
                    SignInDialog signInDialog = new SignInDialog();
                    AppCompatActivity activity = (AppCompatActivity) getContext();
                    signInDialog.show(activity.getSupportFragmentManager(), "sign in");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }

    /**
     * Method to show an alert dialog asking user for the date and time of the test
     * @param registerTest - object which contains the details of the user and the test
     * @param registerTestCollectionReference - database path where the test registration details will be stored
     */
    private void showDateTimeDialog(final RegisterTest registerTest, final CollectionReference registerTestCollectionReference) {
        //creating the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View dialogView = ((MainActivity) getContext()).getLayoutInflater()
                .inflate(R.layout.date_time_input_for_test, null);
        builder.setView(dialogView);
        builder.setTitle(R.string.date_time_dialog_title);
        //setting date and time picker dialogs to the editText fields on alert dialog
        final EditText dateEditText = dialogView.findViewById(R.id.test_date_editText);
        new TestDatePickerHandler(dateEditText, getContext());
        final EditText timeEditText = dialogView.findViewById(R.id.test_time_editText);
        new TestTimePickerHandler(timeEditText, ((MainActivity) getContext()).getSupportFragmentManager());
        builder.setPositiveButton(R.string.register_button_text, null).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        //setting click listener for handling response of positive button when clicker
        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;
                if(dateEditText.getText() == null || dateEditText.getText().toString().isEmpty()) {
                    dateEditText.setError(getContext().getString(R.string.no_date_of_test_selected_error));
                    error = true;
                }
                if(timeEditText.getText() == null || timeEditText.getText().toString().isEmpty()) {
                    timeEditText.setError(getContext().getString(R.string.no_time_of_test_selected_error));
                    error = true;
                }
                if(!error) {
                    registerTest.setDateOfTest(dateEditText.getText().toString());
                    registerTest.setTimeOfTest(timeEditText.getText().toString());
                    registerForTest(registerTest, registerTestCollectionReference);
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * method to execute registration for the genetic test
     * @param registerTest - the object which is sent to the database
     * @param registerTestCollectionReference - path of storage of the data entry in the database
     */
    private void registerForTest(final RegisterTest registerTest, CollectionReference registerTestCollectionReference) {
        registerTestCollectionReference.add(registerTest).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressBar.setVisibility(View.GONE);
                String confirmationString = "";
                switch (registerTest.getTestName()) {
                    case "Diabetes":
                        confirmationString = getContext().getString(R.string.registered_for_diabetes) + "\n" +
                                getContext().getString(R.string.date) + registerTest.getDateOfTest() + "\n" +
                                getContext().getString(R.string.time) + registerTest.getTimeOfTest();
                        break;
                    case "Obesity":
                        confirmationString = getContext().getString(R.string.registered_for_obesity) + "\n" +
                                getContext().getString(R.string.date) + registerTest.getDateOfTest() + "\n" +
                                getContext().getString(R.string.time) + registerTest.getTimeOfTest();
                        break;
                    case "Hypertension":
                        confirmationString = getContext().getString(R.string.registered_for_hypertension) + "\n" +
                                getContext().getString(R.string.date) + registerTest.getDateOfTest() + "\n" +
                                getContext().getString(R.string.time) + registerTest.getTimeOfTest();
                        break;
                }
                Toast.makeText(getContext(), confirmationString, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


}

