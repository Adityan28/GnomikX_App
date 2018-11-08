package com.gnomikx.www.gnomikx.Data;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Class to act as Object for Genetic Tests Registration (POJO)
 */

public class RegisterTest {

    private String testName;
    private String dateOfTest;
    private String timeOfTest;
    private UserDetail userDetails;

    @Exclude private String documentId;

    @ServerTimestamp private Date timestamp;

    public RegisterTest(String testName, String dateOfTest, String timeOfTest, UserDetail userDetails, String documentId, Date timestamp) {
        this.testName = testName;
        this.dateOfTest = dateOfTest;
        this.timeOfTest = timeOfTest;
        this.userDetails = userDetails;
        this.documentId = documentId;
        this.timestamp = timestamp;
    }

    public RegisterTest(String testName, String dateOfTest, String timeOfTest, UserDetail userDetails) {
        this.testName = testName;
        this.dateOfTest = dateOfTest;
        this.timeOfTest = timeOfTest;
        this.userDetails = userDetails;
    }

    public RegisterTest() {
        //necessary empty public constructor
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getDateOfTest() {
        return dateOfTest;
    }

    public void setDateOfTest(String dateOfTest) {
        this.dateOfTest = dateOfTest;
    }

    public UserDetail getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetail userDetail) {
        this.userDetails = userDetail;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeOfTest() {
        return timeOfTest;
    }

    public void setTimeOfTest(String timeOfTest) {
        this.timeOfTest = timeOfTest;
    }
}

