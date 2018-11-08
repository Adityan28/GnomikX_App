package com.gnomikx.www.gnomikx;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment class for handling sign-in
 */

public class FragmentAuthentication extends Fragment {

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "AuthenticationFragment";
    private Button signOutButton;
    private Button signInButton;
    private FirebaseHandler firebaseHandler;
    private TextView signInUsernameText, emailText;
    private ProgressBar progressBar;
    private Button emailButton;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_authentication, container, false);

        signOutButton = rootView.findViewById(R.id.sign_out);
        signInButton = rootView.findViewById(R.id.sign_in_button);
        signInUsernameText = rootView.findViewById(R.id.sign_in_and_username_text);
        emailText = rootView.findViewById(R.id.email_text);
        progressBar = rootView.findViewById(R.id.authentication_state_progress);
        emailButton = rootView.findViewById(R.id.resend_verification_email_button);
        progressBar.setVisibility(View.GONE);

        firebaseHandler = new FirebaseHandler();
        currentUser = firebaseHandler.getFirebaseUser();
        if(currentUser != null) {
            //update UI for signed in users
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            signInUsernameText.setText(currentUser.getDisplayName());
            emailText.setVisibility(View.VISIBLE);
            emailText.setText(currentUser.getEmail());
            if(!currentUser.isEmailVerified()) {
                emailButton.setVisibility(View.VISIBLE);
            } else {
                emailButton.setVisibility(View.GONE);
            }
        } else { //display sign in button, hide other elements
            signOutButton.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
            signInUsernameText.setText(R.string.sign_in_with_gnomikx_account);
            emailText.setVisibility(View.INVISIBLE);
            emailButton.setVisibility(View.GONE);
        }

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MainActivity activity = (MainActivity) getContext();
                progressBar.setVisibility(View.VISIBLE);
                if(currentUser != null) {
                    currentUser.sendEmailVerification()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(activity, R.string.verification_email_sent, Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, e.getMessage());
                                    Toast.makeText(activity, R.string.email_sending_failure_text, Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseHandler.getAuthUI().signOut(getContext())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.GONE);
                                signOutButton.setVisibility(View.GONE);
                                signInUsernameText.setText(getString(R.string.sign_in_with_gnomikx_account));
                                signInButton.setVisibility(View.VISIBLE);
                                emailText.setVisibility(View.INVISIBLE);
                                emailButton.setVisibility(View.GONE);
                            }
                        });
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build()))
                                .build(),
                        RC_SIGN_IN);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            //progress bar should be hidden no matter what the result of authentication
            progressBar.setVisibility(View.GONE);

            //checking result of authentication
            if (resultCode == RESULT_OK) {
                currentUser = firebaseHandler.getFirebaseUser();
                MainActivity activity = (MainActivity) getActivity();
                FirebaseUserMetadata userMetadata = currentUser.getMetadata();
                if (userMetadata != null && userMetadata.getCreationTimestamp() == userMetadata.getLastSignInTimestamp()
                        || activity.getUserDetail() == null) {

                    //new user
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    SignUpFragment signUpFragment = new SignUpFragment();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, signUpFragment, MainActivity.SIGN_UP_FRAGMENT)
                            .addToBackStack(MainActivity.SIGN_UP_FRAGMENT).commit();

                    currentUser.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                    }
                                }
                            });

                    //TODO: verify user's phone number
                }
                signOutButton.setVisibility(View.VISIBLE);
                signInUsernameText.setVisibility(View.VISIBLE);
                signInUsernameText.setText(currentUser.getDisplayName());
                emailText.setVisibility(View.VISIBLE);
                emailText.setText(currentUser.getEmail());
                signInButton.setVisibility(View.GONE);
                if(currentUser.isEmailVerified()) {
                    emailButton.setVisibility(View.GONE);
                } else {
                    emailButton.setVisibility(View.VISIBLE);
                }
                //end FirebaseUi and navigate to correct fragment
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.i("Sign in flow", "Sign in cancelled");
                    return;
                }

                if (response.getError() != null) {
                    if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        Toast.makeText(getContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        Toast.makeText(getContext(), response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            Log.i("Sign in flow", "Unknown sign in response");
        }
    }

    @Override
    public void onResume() {
        currentUser = firebaseHandler.getFirebaseUser();
        super.onResume();
    }
}
