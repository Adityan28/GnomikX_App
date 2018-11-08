package com.gnomikx.www.gnomikx;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

/**
 * Class to display a dialog asking user to sign in
 * to use some features of the app
 */

public class SignInDialog extends DialogFragment {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "SignInDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sign_in_required)
                .setMessage(R.string.sign_in_message_dialog_box)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //initiate authentication flow
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(Arrays.asList(
                                                new AuthUI.IdpConfig.EmailBuilder().build()))
                                        .setLogo(R.mipmap.gnomikx_logo)
                                        .setTheme(R.style.AppTheme)
                                        .build(),
                                RC_SIGN_IN);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //simply dismiss dialog leaving user unauthenticated
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseHandler firebaseHandler = new FirebaseHandler();
                FirebaseUser user = firebaseHandler.getFirebaseUser();
                FirebaseUserMetadata userMetadata = user.getMetadata();
                if (userMetadata != null && userMetadata.getCreationTimestamp() == userMetadata.getLastSignInTimestamp()) {
                    //new user
                    //TODO: Get user's phone number and role
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    SignUpFragment signUpFragment = new SignUpFragment();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.add(android.R.id.content, signUpFragment)
                            .addToBackStack(null).commit();

                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        boolean clicked = showEmailVerificationAlertDialog();
                                        if (clicked) {
                                            //user tried to verify email
                                            //TODO: add some logic
                                        } else {
                                            //user chose to verify later
                                            //TODO: add some logic
                                        }
                                    }
                                }
                            });
                    //TODO: verify user's phone number
                }
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.i("Sign in flow", "Sign in cancelled");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(getContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Log.i("Sign in flow", "Unknown sign in response");
        }
    }

    private boolean showEmailVerificationAlertDialog() {
        final boolean[] clicked = {false};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.email_verification_required)
                .setMessage(R.string.email_verification_message)
                .setPositiveButton(R.string.check_email_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Intent emailLauncher = new Intent(Intent.ACTION_VIEW);
                        emailLauncher.setType("message/rfc822");
                        try{
                            startActivity(emailLauncher);
                        }
                        catch(ActivityNotFoundException e){
                            Toast.makeText(getContext(), R.string.R_string_no_email_application_found, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.verify_later, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clicked[0] = false;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        return clicked[0];
    }
}
