package com.example.rzahab.generator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchUserActivity extends AppCompatActivity implements Serializable {

    Map<String, String> filled_fields;
    Generator app;

    private TextView helloUserText;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private FirebaseUser AuthCurrentUser;
    private String TAG;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        TAG = this.getLocalClassName();

        app = ((Generator) this.getApplication());

        authenticateUser();

    }

    public void authenticateUser() {
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        helloUserText = (TextView) findViewById(R.id.hello_label);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                AuthCurrentUser = firebaseAuth.getCurrentUser();
                if (AuthCurrentUser == null) {
                    // if user is null launch login activity
                    startActivity(new Intent(SearchUserActivity.this, LoginActivity.class));
                    finish();
                } else {
                    helloUserText.setText(getString(R.string.hello) + AuthCurrentUser.getEmail() + "");
                    progressBar.setVisibility(View.GONE);
                }
            }
        };
    }

    public void search(View v) {
        if (!validateInput()) {
            Toast.makeText(this, "Make sure to provide at least 3 out of 4 names", Toast.LENGTH_LONG).show();
        } else {

            HashMap<String, String> userData = getUserData();
            final String gender_search = userData.get("gender");
            final String dob_search = "22071989";//userData.get("dob");

            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference myRef = database.getReference("users").child(gender_search).child(dob_search);

            final SuggestionLibrary sl = new SuggestionLibrary();
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ArrayList<SuggestedUser> users_suggested = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        HashMap<String, String> currentUser = new HashMap<>();
                        String ID = postSnapshot.getKey();

                        Log.d(TAG, "==== ID: " + ID + "====");
                        currentUser.put("ID", ID);
                        for (DataSnapshot userAttribute : postSnapshot.getChildren()) {
                            //Log.d(TAG, "=======" + userAttribute.getKey() + " : " + userAttribute.getValue());
                            currentUser.put(userAttribute.getKey() + "", userAttribute.getValue() + "");
                        }

                        double rate = sl.getDifferenceRate(filled_fields, currentUser);
                        currentUser.put(("rate"), rate + "");

                        if (rate < app.getRateThreshold()) {
                            Log.d(TAG, "suggested : " + ID + " with: " + rate);
                            SuggestedUser u = new SuggestedUser(currentUser);
                            users_suggested.add(u);
                        }
                        Log.d(TAG, "==== ====");
                    }
                    if (users_suggested.size() > 0) {
                        app.setSuggestedUsers(users_suggested);

                        Intent i = new Intent(SearchUserActivity.this, ListSuggestedActivity.class);
                        i.putExtra("gender", gender_search);
                        i.putExtra("dob", dob_search);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(SearchUserActivity.this, "Non found", Toast.LENGTH_LONG).show();
                        addNew();
                    }
                }


                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public boolean validateInput() {
        filled_fields = new HashMap<>();
        EditText first_name_txt, last_name_txt, father_name_txt, mother_name_txt;
        first_name_txt = (EditText) findViewById(R.id.first_name);
        last_name_txt = (EditText) findViewById(R.id.last_name);
        father_name_txt = (EditText) findViewById(R.id.father_name);
        mother_name_txt = (EditText) findViewById(R.id.mother_name);

        int required = 0;

        if (first_name_txt.getText().toString().trim().length() != 0) {
            filled_fields.put("first_name", first_name_txt.getText().toString());
            required++;
        }
        if (last_name_txt.getText().toString().trim().length() != 0) {
            filled_fields.put("last_name", last_name_txt.getText().toString());
            required++;
        }
        if (father_name_txt.getText().toString().trim().length() != 0) {
            filled_fields.put("father_name", father_name_txt.getText().toString());
            required++;
        }
        if (mother_name_txt.getText().toString().trim().length() != 0) {
            filled_fields.put("mother_name", mother_name_txt.getText().toString());
            required++;
        }

        return (required >= 3);
    }

    public HashMap<String, String> getUserData() {

        DatePicker dob_dialog = (DatePicker) findViewById(R.id.dob);
        String month = String.format("%02d", dob_dialog.getMonth() + 1);

        String dob = dob_dialog.getDayOfMonth() + "" + month + "" + dob_dialog.getYear();

        RadioGroup gender_group = (RadioGroup) findViewById(R.id.gender);
        int selectedGender = gender_group.getCheckedRadioButtonId();

        String gender = (selectedGender == R.id.male) ? "male" : "female";

        HashMap<String, String> userData = new HashMap<>();

        userData.putAll(filled_fields);
        userData.put("dob", dob);
        userData.put("gender", gender);

        app.setUserData(userData);

        return userData;
    }


    //sign out method
    public void signOut(View v) {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public void addNew() {
        startActivity(new Intent(this, NewUserActivity.class));
        finish();
    }


}
