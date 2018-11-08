package com.gnomikx.www.gnomikx.Handlers;

import android.support.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.gnomikx.www.gnomikx.Data.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Collection;

/**
 * Class to handle instances of all Firebase Properties
 */

public class FirebaseHandler {

    public FirebaseHandler() {
    }

    /**
     * method to get an instance of FirebaseFirestore
     * @return - an instance of Firebase Firestore
     */
    public FirebaseFirestore getFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    /**
     * method to get an instance of Firebase Auth
     * @return - an instance of FirebaseAuth
     */
    public FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }


    /**
     * method to get an instance of FirebaseStorage
     * @return - an instance of FirebaseStorage
     */
    public FirebaseStorage getFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    /**
     * method to return an objet of StorageReference
     * @return - an object of StorageReference
     */
    public StorageReference getStorageReference() {
        return getFirebaseStorage().getReference();
    }

    /**
     * method to return an object of the current user if any (current user == null means not signed in)
     * @return - an object of current user
     */
    public FirebaseUser getFirebaseUser() {
        return getFirebaseAuth().getCurrentUser();
    }

    /**
     * method to return the CollectionReference for blogs
     * @return - the CollectionReference for blogs
     */
    public CollectionReference getBlogCollectionReference() {
        return getFirebaseFirestore().collection("Blogs");
    }

    public CollectionReference getQueryCollectionRefrence(){
        return getFirebaseFirestore().collection("Queries");
    }

    public CollectionReference getRegisterPatientCollectionReference(){
        return getFirebaseFirestore().collection("Registrations");
    }

    public CollectionReference getRegisterGeneticTestCollectionReference(){
        return getFirebaseFirestore().collection("Genetic Test Registrations");
    }

    public CollectionReference getUserDetailsCollectionReference() {
        return getFirebaseFirestore().collection("User Details");
    }

    public CollectionReference getFavoritesCollectionReference() {
        return getFirebaseFirestore().collection("Favorites");
    }

    /**
     * method to return the Storage reference for the blog images
     * @return - the Storage reference for the blog images
     */
    public StorageReference getBlogImagesStorageReference() {
        return getStorageReference().child("blogImages");
    }

    public DocumentReference getUserDetailsDocumentRef() {
        CollectionReference userDetailsRef = getUserDetailsCollectionReference();
        return  userDetailsRef.document(getFirebaseUser().getUid());
    }

    public AuthUI getAuthUI() {
        return AuthUI.getInstance();
    }

    public CollectionReference getReportsCollectionReference() {
        return getFirebaseFirestore().collection("Reports");
    }

    public StorageReference getReportsStorageReference() {
        return getStorageReference().child("Reports");
    }
}
