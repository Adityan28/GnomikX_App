package com.gnomikx.www.gnomikx;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gnomikx.www.gnomikx.ActivityWidgetHandler.MakeVisible;
import com.gnomikx.www.gnomikx.Data.UserDetail;
import com.gnomikx.www.gnomikx.Handlers.FirebaseHandler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements MakeVisible {

    private static final String TAG = "MainActivity";
    public static final String DISPLAY_BLOG_TAG = "DisplayBlogFragment";
    public static final String DISPLAY_USER_FRAGMENT = "DisplayUserFragment";
    public static final String QUERY_RESPONSE_FRAGMENT = "QueryResponseFragment";
    public static final String VIEW_QUERY_FRAGMENT = "ViewQueryFragment";
    public static final String SIGN_UP_FRAGMENT = "SignUpFragment";
    private DrawerLayout mDrawerLayout;
    public UserDetail userDetail;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    public static final String blogTag = "BlogFragment";
    private NavigationView navigationView;

    //gender constants
    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;
    public static final int GENDER_OTHER = 2;

    //Role constants
    public static final String ROLE_PATIENT = "Patient";
    public static final String ROLE_DOCTOR = "Doctor";
    public static final String ROLE_ADMIN = "Admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDetail = null;

        FirebaseHandler handler = new FirebaseHandler();
        firebaseAuth = handler.getFirebaseAuth();

        //setting toolbar as the Activity's ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);

        //setting an auth state listener to fetch userDetails from database and
        //to display the correct elements on the navigation drawer
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i(TAG, "Auth state listener called");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    if(userDetail == null) {
                        FirebaseHandler handler = new FirebaseHandler();
                        DocumentReference userDetailsDocumentReference = handler.getUserDetailsDocumentRef();
                        userDetailsDocumentReference.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            UserDetail detail = documentSnapshot.toObject(UserDetail.class);
                                            setUserDetail(detail);
                                            showMenuAndNavFields(detail);
                                        }
                                    }
                                });
                    } else {
                        showMenuAndNavFields(userDetail);
                    }
                }
                else { //user is not signed in
                    hideMenuAndNavFields();
                    userDetail = null; //set userDetails to null, if not already set
                }
            }
        };

        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        //displaying pre_loader fragment
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PreLoader preLoader = new PreLoader();
        fragmentTransaction.add(R.id.fragment_container, preLoader);
        actionbar.hide();
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();

        FirebaseUser user = handler.getFirebaseUser();
        if(user != null && userDetail != null) {
            DocumentReference userDetailsDocRef = handler.getUserDetailsDocumentRef();
            userDetailsDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()) {
                        userDetail = documentSnapshot.toObject(UserDetail.class);
                    }
                }
            });
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        /*
                                use these lines (with alterations to call the correct class
                                for switching between fragments according to the element
                                selected by the user. Remember to create a new FragmentTransaction
                                every time, as reusing one such instance can throw an exception
                                causing app to crash
                        */
                        displaySelectedFragment(menuItem.getItemId(), fragmentManager);
                        return true;
                    }
                });
    }

    public void displaySelectedFragment(int id, FragmentManager fragmentManager) {
        switch(id) {
            case R.id.nav_home_id: {
                showFragment(new BlogFragment(), fragmentManager);
            } break;
            case R.id.nav_sign_id: {
                showFragment(new FragmentAuthentication(), fragmentManager);
            } break;
            case R.id.nav_bmi_id: {
                showFragment(new BodyMassIndexFragment(), fragmentManager);
            } break;
            case R.id.nav_genetic_test_id: {
                showFragment(new FragmentGeneticTests(), fragmentManager);
            } break;
            case R.id.nav_register_patients_id: {
                showFragment(new FragmentRegisterPatients(), fragmentManager);
            } break;
            case R.id.nav_queries_id: {
                showFragment(new FragmentQueries(), fragmentManager);
            } break;
            case R.id.nav_submit_blog_id: {
                showFragment(new FragmentSubmitBlog(), fragmentManager);
            } break;
            case R.id.nav_my_account_id: {
                showFragment(new FragmentMyAccount(), fragmentManager);
            } break;
            case R.id.nav_see_users_id: {
                showFragment(new AllUsersFragment(), fragmentManager);
            } break;
            case R.id.nav_see_queries: {
                showFragment(new AnswerQueriesFragment(), fragmentManager);
            } break;
            case R.id.nav_submitted_blogs: {
                showFragment(new ApproveBlogsFragment(), fragmentManager);
            } break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }
    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    /**
     * method to make the action bar visible when needed
     */
    @Override
    public void makeVisible() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.show();
    }

    @Override
    protected void onStart() {
        firebaseAuth.addAuthStateListener(authStateListener);
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume() called");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null && !user.isEmailVerified()) {
            user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "FirebaseUser reloaded successfully");
                }
            });
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        firebaseAuth.removeAuthStateListener(authStateListener);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        //close drawer if it is open, else display home fragment if it is not displayed, else close app
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(Gravity.START, true);
            Log.i(TAG, "Navigation drawer closed");
        } else if(getSupportFragmentManager().findFragmentByTag(DISPLAY_BLOG_TAG) != null ||
                getSupportFragmentManager().findFragmentByTag(DISPLAY_USER_FRAGMENT) != null ||
                getSupportFragmentManager().findFragmentByTag(QUERY_RESPONSE_FRAGMENT) != null ||
                getSupportFragmentManager().findFragmentByTag(VIEW_QUERY_FRAGMENT) != null ||
                getSupportFragmentManager().findFragmentByTag(SIGN_UP_FRAGMENT) != null) {
            //blog contents are being displayed
            Log.i(TAG, "Blog details or User details fragments closed");
            super.onBackPressed();
        }
        else if(getSupportFragmentManager().findFragmentByTag(blogTag) == null) {
            Log.i(TAG, "displaying home screen");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new BlogFragment(), blogTag);
            fragmentTransaction.commit();
            MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_home_id);
            menuItem.setChecked(true);
        }
        else {
            super.onBackPressed();
        }
    }

    /**
     * Method to hide fields that should not be shown to an unauthenticated user
     * and add placeholder text to navigation drawer heading
     */
    private void hideMenuAndNavFields() {
        //setting placeholder elements as text for username and user email in nav_bar
        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.nav_drawer_user_name);
        usernameText.setText(getString(R.string.gnomikx_user));
        TextView userEmail = headerView.findViewById(R.id.nav_drawer_user_email);
        userEmail.setText(R.string.gnomikx_email_id);

        //setting sign in label and icon for FragmentAuthentication
        MenuItem authentication = navigationView.getMenu().findItem(R.id.nav_sign_id);
        authentication.setTitle(getString(R.string.nav_sign));
        authentication.setIcon(R.drawable.ic_sign_in_24dp);

        //hiding My Account page
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_my_account_id);
        menuItem.setVisible(false);

        //hiding register patients menu
        MenuItem registerPatients = navigationView.getMenu().findItem(R.id.nav_register_patients_id);
        registerPatients.setVisible(false);

        //displaying some required elements
        MenuItem geneticTests = navigationView.getMenu().findItem(R.id.nav_genetic_test_id);
        geneticTests.setVisible(true);
        MenuItem postQuery = navigationView.getMenu().findItem(R.id.nav_queries_id);
        postQuery.setVisible(true);

        //admin level menu items
        MenuItem seeUsers = navigationView.getMenu().findItem(R.id.nav_see_users_id);
        seeUsers.setVisible(false);

        MenuItem seeQueries = navigationView.getMenu().findItem(R.id.nav_see_queries);
        seeQueries.setVisible(false);

        MenuItem seeBlogs = navigationView.getMenu().findItem(R.id.nav_submitted_blogs);
        seeBlogs.setVisible(false);
    }

    /**
     * Method to show the appropriate menu fields for the current user
     * and add user's details to the navigation drawer
     * @param detail - contains the details of the user
     */
    public void showMenuAndNavFields(UserDetail detail) {
        //setting username and user email to nav_drawer fields
        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.nav_drawer_user_name);
        usernameText.setText(detail.getUserName());
        TextView userEmail = headerView.findViewById(R.id.nav_drawer_user_email);
        userEmail.setText(detail.getUserEmailID());

        Menu menu = navigationView.getMenu();

        //setting sign out label and icon for FragmentAuthentication
        MenuItem authentication = menu.findItem(R.id.nav_sign_id);
        authentication.setTitle(getString(R.string.sign_out_button_text));
        authentication.setIcon(R.drawable.ic_sign_out_24dp);

        //hide register patients option for patients, reveal it for doctors
        switch (userDetail.getRole()) {
            case ROLE_PATIENT: {
                MenuItem menuItem = menu.findItem(R.id.nav_register_patients_id);
                menuItem.setVisible(false);

                //making My Account section visible
                MenuItem myAccount = menu.findItem(R.id.nav_my_account_id);
                myAccount.setVisible(true);

                MenuItem geneticTests = menu.findItem(R.id.nav_genetic_test_id);
                geneticTests.setVisible(true);

                MenuItem postQuery = menu.findItem(R.id.nav_queries_id);
                postQuery.setVisible(true);

                //admin level menu items
                MenuItem seeUsers = navigationView.getMenu().findItem(R.id.nav_see_users_id);
                seeUsers.setVisible(false);

                MenuItem seeQueries = navigationView.getMenu().findItem(R.id.nav_see_queries);
                seeQueries.setVisible(false);

                MenuItem seeBlogs = navigationView.getMenu().findItem(R.id.nav_submitted_blogs);
                seeBlogs.setVisible(false);
                break;
            }
            case ROLE_DOCTOR: {
                MenuItem registerPatients = menu.findItem(R.id.nav_register_patients_id);
                registerPatients.setVisible(true);

                //making My Account section visible
                MenuItem myAccount = menu.findItem(R.id.nav_my_account_id);
                myAccount.setVisible(true);

                //admin level menu items
                MenuItem seeUsers = navigationView.getMenu().findItem(R.id.nav_see_users_id);
                seeUsers.setVisible(false);

                MenuItem seeQueries = navigationView.getMenu().findItem(R.id.nav_see_queries);
                seeQueries.setVisible(false);

                MenuItem seeBlogs = navigationView.getMenu().findItem(R.id.nav_submitted_blogs);
                seeBlogs.setVisible(false);

                MenuItem geneticTests = menu.findItem(R.id.nav_genetic_test_id);
                geneticTests.setVisible(true);

                MenuItem postQuery = menu.findItem(R.id.nav_queries_id);
                postQuery.setVisible(true);

                break;
            }
            case ROLE_ADMIN:
                //admin level menu items
                MenuItem seeUsers = menu.findItem(R.id.nav_see_users_id);
                seeUsers.setVisible(true);
                MenuItem seeQueries = menu.findItem(R.id.nav_see_queries);
                seeQueries.setVisible(true);
                MenuItem seeBlogs = menu.findItem(R.id.nav_submitted_blogs);
                seeBlogs.setVisible(true);

                //admin won't register for genetic tests, post queries, register patients or need "my account"
                MenuItem geneticTests = menu.findItem(R.id.nav_genetic_test_id);
                geneticTests.setVisible(false);
                MenuItem postQuery = menu.findItem(R.id.nav_queries_id);
                postQuery.setVisible(false);
                MenuItem registerPatients = menu.findItem(R.id.nav_register_patients_id);
                registerPatients.setVisible(false);
                MenuItem myAccount = menu.findItem(R.id.nav_my_account_id);
                myAccount.setVisible(false);
                break;
        }
    }

    /**
     * Method to display the correct fragment
     * @param fragment - the fragment to be displayed
     * @param fragmentManager - an instance of SupportFragmentManager, to manage the fragments
     */
    private void showFragment(Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //remove DisplayBlogContentsFragment from back stack if it exists in back stack
        if(fragmentManager.findFragmentByTag(DISPLAY_BLOG_TAG ) != null) {
            Log.i(TAG, "Removed " + DISPLAY_BLOG_TAG + " from back stack");
            fragmentTransaction.remove(fragmentManager.findFragmentByTag(DISPLAY_BLOG_TAG));
            fragmentManager.popBackStackImmediate(DISPLAY_BLOG_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if(fragmentManager.findFragmentByTag(DISPLAY_USER_FRAGMENT ) != null) {
            Log.i(TAG, "Removed " + DISPLAY_USER_FRAGMENT + " from back stack");
            fragmentTransaction.remove(fragmentManager.findFragmentByTag(DISPLAY_USER_FRAGMENT));
            fragmentManager.popBackStackImmediate(DISPLAY_USER_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if(fragmentManager.findFragmentByTag(QUERY_RESPONSE_FRAGMENT ) != null) {
            Log.i(TAG, "Removed " + QUERY_RESPONSE_FRAGMENT + " from back stack");
            fragmentTransaction.remove(fragmentManager.findFragmentByTag(QUERY_RESPONSE_FRAGMENT));
            fragmentManager.popBackStackImmediate(QUERY_RESPONSE_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if(fragmentManager.findFragmentByTag(VIEW_QUERY_FRAGMENT ) != null) {
            Log.i(TAG, "Removed " + VIEW_QUERY_FRAGMENT + " from back stack");
            fragmentTransaction.remove(fragmentManager.findFragmentByTag(VIEW_QUERY_FRAGMENT));
            fragmentManager.popBackStackImmediate(VIEW_QUERY_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if(fragmentManager.findFragmentByTag(SIGN_UP_FRAGMENT ) != null) {
            Log.i(TAG, "Removed " + SIGN_UP_FRAGMENT + " from back stack");
            fragmentTransaction.remove(fragmentManager.findFragmentByTag(SIGN_UP_FRAGMENT));
            fragmentManager.popBackStackImmediate(SIGN_UP_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        // Replace whatever is in the fragment_container view with this fragment,
        // and not allowing addition of the transaction to the back stack.
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.disallowAddToBackStack();

        //commit changes - this will inflate the fragment layout
        fragmentTransaction.commit();
    }
}
