package com.gnomikx.www.gnomikx;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gnomikx.www.gnomikx.Data.QueryDetails;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class QueryResponseFragment extends Fragment {


    public QueryResponseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_query_response, container, false);

        final QueryDetails queryDetails = new QueryDetails();
        Bundle bundle = getArguments();
        queryDetails.setUserID(bundle.getString("User Id"));
        queryDetails.setUsername(bundle.getString("User name"));
        queryDetails.setTimestamp((Date) bundle.getSerializable("Timestamp"));
        queryDetails.setTitle(bundle.getString("Title"));
        queryDetails.setBody(bundle.getString("Body"));
        queryDetails.setDocumentId(bundle.getString("documentId"));

        TextView dateText = rootView.findViewById(R.id.query_response_date);
        dateText.setText(bundle.getString("Date"));

        TextView nameText = rootView.findViewById(R.id.query_response_username);
        nameText.setText(queryDetails.getUsername());

        TextView titleText = rootView.findViewById(R.id.query_response_heading);
        titleText.setText(queryDetails.getTitle());

        TextView bodyText = rootView.findViewById(R.id.query_response_body);
        bodyText.setText(queryDetails.getBody());

        final EditText responseText = rootView.findViewById(R.id.query_response_edit_text);

        final ProgressBar progressBar = rootView.findViewById(R.id.query_response_progress_bar);
        progressBar.setVisibility(View.GONE);

        final MainActivity activity = (MainActivity) getActivity();
        Button postResponse = rootView.findViewById(R.id.post_response_button);
        postResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(responseText.getText() == null || responseText.getText().toString().equals("") ||
                        responseText.getText().toString().isEmpty()) {
                    responseText.setError(getString(R.string.no_response_entered_error));
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseHandler handler = new FirebaseHandler();
                    DocumentReference queryRef = handler.getQueryCollectionRefrence().document(queryDetails.getDocumentId());
                    queryRef.update("response", responseText.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.GONE);
                                    responseText.setText("");
                                    Toast.makeText(activity, R.string.response_posted_message, Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        return rootView;
    }

}
