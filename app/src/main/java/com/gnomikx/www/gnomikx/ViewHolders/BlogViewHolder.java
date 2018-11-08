package com.gnomikx.www.gnomikx.ViewHolders;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gnomikx.www.gnomikx.R;

/**
 * Class to act as the view holder for the RecyclerView of BlogFragment
 */

public class BlogViewHolder extends RecyclerView.ViewHolder {

    private TextView headLine, trailText, date, noDataText;
    private ImageView headlineImage;

    public BlogViewHolder(View itemView) {
        super(itemView);
        headLine = itemView.findViewById(R.id.headline);
        trailText = itemView.findViewById(R.id.trailText);
        date = itemView.findViewById(R.id.date);
        headlineImage = itemView.findViewById(R.id.headline_image);
        noDataText = itemView.findViewById(R.id.my_blog_no_data);
    }

    public TextView getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine.setText(headLine);
    }

    public TextView getTrailText() {
        return trailText;
    }

    public void setTrailText(String trailText) {
        this.trailText.setText(trailText);
    }

    public TextView getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date.setText(date);
    }

    public ImageView getHeadlineImage() {
        return headlineImage;
    }

    public TextView getNoDataText() {
        return noDataText;
    }

    public void setNoDataText(TextView noDataText) {
        this.noDataText = noDataText;
    }

    public void setHeadlineImage(Bitmap headlineImage) {
        this.headlineImage.setImageBitmap(headlineImage);
    }

    public void bind(String headLine, String trailText, String date) {
        this.headLine.setText(headLine);
        this.trailText.setText(trailText);
        this.date.setText(date);
    }
}
