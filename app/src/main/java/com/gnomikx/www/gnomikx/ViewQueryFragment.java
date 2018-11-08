package com.gnomikx.www.gnomikx;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gnomikx.www.gnomikx.Data.QueryDetails;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewQueryFragment extends Fragment {


    public ViewQueryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_query, container, false);

        final QueryDetails queryDetails = new QueryDetails();
        Bundle bundle = getArguments();
        queryDetails.setTitle(bundle.getString("Title"));
        queryDetails.setBody(bundle.getString("Body"));
        queryDetails.setResponse(bundle.getString("Response"));

        TextView dateText = rootView.findViewById(R.id.view_query_date);
        dateText.setText(bundle.getString("Date"));

        TextView titleText = rootView.findViewById(R.id.view_query_heading);
        titleText.setText(queryDetails.getTitle());

        TextView bodyText = rootView.findViewById(R.id.view_query_body);
        bodyText.setText(queryDetails.getBody());

        TextView responseText = rootView.findViewById(R.id.view_query_response);
        if(queryDetails.getResponse() != null) {
            responseText.setText(queryDetails.getResponse());
        }

        return rootView;
    }
}
