package com.gnomikx.www.gnomikx.Data;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Class to act as the object for the details of the query(POJO).
 */

public class QueryDetails {

    private String title;
    private String body;
    private String username;
    private String userID;
    private String response;
    @Exclude
    private String documentId;
    @ServerTimestamp
    private Date timestamp;

    public QueryDetails(){
        //necessary empty public constructor
    }

    public QueryDetails(String title, String body, String username, String userID,String documentID, Date timestamp, String response){
        this.title = title;
        this.body = body;
        this.username = username;
        this.userID = userID;
        this.documentId = documentID;
        this.timestamp = timestamp;
        this.response = response;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
