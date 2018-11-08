package com.gnomikx.www.gnomikx.Data;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.firestore.Exclude;

/**
 * Class to act as data object for uploading and downloading user reports (POJO)
 */

public class UserReports {
    private String documentName;
    private UserDetail userDetail;
    @Exclude private String documentId;

    public UserReports(String documentName, UserDetail userDetail, String documentId) {
        this.documentName = documentName;
        this.userDetail = userDetail;
        this.documentId = documentId;
    }

    public UserReports(String documentName, UserDetail userDetail) {
        this.documentName = documentName;
        this.userDetail = userDetail;
    }

    public UserReports() {
        //necessary empty public constructor
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
