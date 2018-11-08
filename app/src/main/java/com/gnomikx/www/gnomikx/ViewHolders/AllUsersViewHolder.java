package com.gnomikx.www.gnomikx.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gnomikx.www.gnomikx.R;

/**
 * Class to act as view holder for Upload Report Fragment
 */


public class AllUsersViewHolder extends RecyclerView.ViewHolder {

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userPhoneTextView;

    public AllUsersViewHolder(View itemView) {
        super(itemView);
        this.userNameTextView = itemView.findViewById(R.id.all_users_user_name);
        this.userEmailTextView = itemView.findViewById(R.id.all_users_email_id);
        this.userPhoneTextView = itemView.findViewById(R.id.all_users_phone);
    }

    public TextView getUserNameTextView() {
        return userNameTextView;
    }

    public void setUserNameTextView(TextView userNameTextView) {
        this.userNameTextView = userNameTextView;
    }

    public TextView getUserEmailTextView() {
        return userEmailTextView;
    }

    public void setUserEmailTextView(TextView userEmailTextView) {
        this.userEmailTextView = userEmailTextView;
    }

    public TextView getUserPhoneTextView() {
        return userPhoneTextView;
    }

    public void setUserPhoneTextView(TextView userPhoneTextView) {
        this.userPhoneTextView = userPhoneTextView;
    }

    public void bind(String userName, String userEmail, String userPhone) {
        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);
        userPhoneTextView.setText(userPhone);
    }
}
