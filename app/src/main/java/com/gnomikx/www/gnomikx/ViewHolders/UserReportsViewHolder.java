package com.gnomikx.www.gnomikx.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gnomikx.www.gnomikx.R;

/**
 * Created by Mayank on 25.2.18.
 */

public class UserReportsViewHolder extends RecyclerView.ViewHolder {

    TextView reportNameTextView;


    public UserReportsViewHolder(View itemView) {
        super(itemView);
        reportNameTextView = itemView.findViewById(R.id.my_reports_text);
    }

    public TextView getReportNameTextView() {
        return reportNameTextView;
    }

    public void setReportNameTextView(TextView reportNameTextView) {
        this.reportNameTextView = reportNameTextView;
    }

    public void bind(String reportName) {
        reportNameTextView.setText(reportName);
    }
}
