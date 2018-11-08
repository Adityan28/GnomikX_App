package com.gnomikx.www.gnomikx.Data;

import com.google.firebase.firestore.Exclude;

/**
 * class to act as object for user details (POJO)
 */

public class UserDetail {

    private String userName;
    private String userEmailID;
    private String phoneNumber;
    private String dateOfBirth;
    private int gender;
    private String role;

    @Exclude private String documentId;

    public UserDetail(){

    }

    public UserDetail(String userName, String userEmailID, String phoneNumber, String dateOfBirth, int gender, String role, String documentId) {
        this.userName = userName;
        this.userEmailID = userEmailID;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.role = role;
        this.documentId = documentId;
    }

    public UserDetail(String userName, String userEmailID, String phoneNumber, String dateOfBirth, int gender, String role) {
        this.userName = userName;
        this.userEmailID = userEmailID;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmailID() {
        return userEmailID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmailID(String userEmailID) {
        this.userEmailID = userEmailID;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
