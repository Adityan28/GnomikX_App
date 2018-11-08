package com.gnomikx.www.gnomikx.Data;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Class to act as the object for the data of the blog (POJO)
 */

public class Blog {
    private String userId; //the UserId of the uploader
    private String headline;
    private String trailText;
    private String body;
    private String imageName;
    private boolean diabetesTag;
    private boolean obesityTag;
    private boolean generalHealthTag;
    private String documentId;
    private boolean approved;

    @ServerTimestamp private Date timestamp;

    public Blog() {
        //necessary empty public constructor
    }

    public Blog(String userId, String headline, String trailText,
                String body, String imageName, boolean diabetesTag,
                boolean obesityTag, boolean generalHealthTag, Date timestamp,
                String documentId) {
        this.userId = userId;
        this.headline = headline;
        this.trailText = trailText;
        this.body = body;
        this.imageName = imageName;
        this.diabetesTag = diabetesTag;
        this.obesityTag = obesityTag;
        this.generalHealthTag = generalHealthTag;
        this.timestamp = timestamp;
        this.documentId = documentId;
    }

    public Blog(String userId, String headline, String trailText, String body,
                String imageName, boolean diabetesTag, boolean obesityTag,
                boolean generalHealthTag, String documentId, boolean approved, Date timestamp) {
        this.userId = userId;
        this.headline = headline;
        this.trailText = trailText;
        this.body = body;
        this.imageName = imageName;
        this.diabetesTag = diabetesTag;
        this.obesityTag = obesityTag;
        this.generalHealthTag = generalHealthTag;
        this.documentId = documentId;
        this.approved = approved;
        this.timestamp = timestamp;
    }

    public String getHeadline() {
        return headline;
    }

    public String getTrailText() {
        return trailText;
    }

    public String getBody() {
        return body;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setHeadline(String mHeadline) {
        this.headline = mHeadline;
    }

    public void setTrailText(String mTrailText) {
        this.trailText = mTrailText;
    }

    public void setBody(String mBody) {
        this.body = mBody;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public boolean isDiabetesTag() {
        return diabetesTag;
    }

    public void setDiabetesTag(boolean diabetesTag) {
        this.diabetesTag = diabetesTag;
    }

    public boolean isObesityTag() {
        return obesityTag;
    }

    public void setObesityTag(boolean obesityTag) {
        this.obesityTag = obesityTag;
    }

    public boolean isGeneralHealthTag() {
        return generalHealthTag;
    }

    public void setGeneralHealthTag(boolean generalHealthTag) {
        this.generalHealthTag = generalHealthTag;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
