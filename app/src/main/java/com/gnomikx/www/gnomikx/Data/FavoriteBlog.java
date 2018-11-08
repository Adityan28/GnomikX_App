package com.gnomikx.www.gnomikx.Data;

import java.util.Date;

/**
 * Class to act as object for favorite blog (POJO)
 */

public class FavoriteBlog {

    private String userId;
    private Blog favBlog;
    private Date timestamp;

    public FavoriteBlog() {
        //necessary empty public constructor
    }

    public FavoriteBlog(String userId, Blog favBlog, Date timestamp) {
        this.userId = userId;
        this.favBlog = favBlog;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Blog getFavBlog() {
        return favBlog;
    }

    public void setFavBlog(Blog favBlog) {
        this.favBlog = favBlog;
    }
}
