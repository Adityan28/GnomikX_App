package com.gnomikx.www.gnomikx;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gnomikx.www.gnomikx.Data.UserReports;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.gnomikx.www.gnomikx.ViewHolders.UserReportsViewHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * Fragment class for displaying user's reports
 */

public class FragmentMyReports extends Fragment {

    private static final String TAG = "MyReportsFragment";
    private FirestoreRecyclerAdapter<UserReports, UserReportsViewHolder> adapter;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_reports, container, false);

        final FirebaseHandler handler = new FirebaseHandler();
        user = handler.getFirebaseUser();

        RecyclerView recyclerView = rootView.findViewById(R.id.my_report_recyclerview);
        final TextView noDataTextView = rootView.findViewById(R.id.my_report_no_data);

        if(user != null) {

            Query query = handler.getReportsCollectionReference().whereEqualTo("userDetail.userName", user.getDisplayName());

            FirestoreRecyclerOptions<UserReports> options = new FirestoreRecyclerOptions.Builder<UserReports>()
                    .setQuery(query, UserReports.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<UserReports, UserReportsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull UserReportsViewHolder holder, int position, @NonNull final UserReports model) {
                    holder.itemView.setTag(model.getDocumentId());
                    holder.bind(model.getDocumentName());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StorageReference reportRef = handler.getReportsStorageReference().child(model.getDocumentName());

                            reportRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.parse(uri.toString()), "application/pdf");
                                    try {
                                        startActivity(Intent.createChooser(intent, getString(R.string.open_report_chooser_title)));
                                    } catch (ActivityNotFoundException e) {
                                        Log.i(TAG, e.getMessage());
                                        MainActivity activity = (MainActivity) getActivity();
                                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors
                                    MainActivity activity = (MainActivity) getActivity();
                                    Toast.makeText(activity, R.string.file_could_not_be_opened, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }

                @Override
                public void onDataChanged() {
                    if(noDataTextView != null) {
                        if (getItemCount() == 0) {
                            noDataTextView.setVisibility(View.VISIBLE);
                        } else {
                            noDataTextView.setVisibility(View.GONE);
                        }
                    }
                    super.onDataChanged();
                }

                @Override
                public UserReportsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.my_reports_list_item, parent, false);

                    return new UserReportsViewHolder(view);
                }
            };
            MainActivity activity = (MainActivity) getActivity();
            LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
            recyclerView.setLayoutManager(layoutManager);
            assert activity != null;
            recyclerView.addItemDecoration(new DividerItemDecoration(activity, layoutManager.getOrientation()));
            recyclerView.setAdapter(adapter);

        } else {
            Toast.makeText(getContext(), "You need to be signed in to see this", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (user != null && adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(user != null && adapter != null) {
            adapter.stopListening();
        }
    }
}
