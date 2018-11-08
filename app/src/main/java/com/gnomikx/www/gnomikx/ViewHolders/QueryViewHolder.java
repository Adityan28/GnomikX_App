package com.gnomikx.www.gnomikx.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gnomikx.www.gnomikx.R;

/**
 * Class to act as ViewHolder for MyQueries RecyclerView
 */

public class QueryViewHolder extends RecyclerView.ViewHolder{

    private TextView mTitle;
    private TextView mBody;
    private TextView mDate;

    public QueryViewHolder(View itemView) {
        super(itemView);
        mTitle = itemView.findViewById(R.id.query_title);
        mBody = itemView.findViewById(R.id.query_body);
        mDate = itemView.findViewById(R.id.query_timestamp);
    }

    public TextView getmTitle() {
        return mTitle;
    }

    public TextView getmBody() {
        return mBody;
    }

    public void setmTitle(TextView mTitle) {
        this.mTitle = mTitle;
    }

    public void setmBody(TextView mBody) {
        this.mBody = mBody;
    }

    public TextView getmDate() {
        return mDate;
    }

    public void setmDate(TextView mDate) {
        this.mDate = mDate;
    }

    public void bind(String timestamp, String title, String body) {
        this.mDate.setText(timestamp);
        this.mTitle.setText(title);
        this.mBody.setText(body);
    }
}
