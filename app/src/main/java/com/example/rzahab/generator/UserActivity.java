package com.example.rzahab.generator;

import android.content.Intent;
import android.os.Bundle;
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

public class UserActivity extends AppCompatActivity implements Serializable {

    Map<String, String> filled_fields;
    HashMap<String, HashMap<String, String>> suggested_users;
    Generator app;

    private TextView helloUserText;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private FirebaseUser CurrentUser;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        TAG = this.getLocalClassName().toString();

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
                CurrentUser = firebaseAuth.getCurrentUser();
                if (CurrentUser == null) {
                    // if user is null launch login activity
                    startActivity(new Intent(UserActivity.this, LoginActivity.class));
                    finish();
                } else {
                    helloUserText.setText(getString(R.string.hello) + CurrentUser.getEmail() + "");
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

                        Intent i = new Intent(UserActivity.this, ListSuggestedActivity.class);
                        i.putExtra("gender", gender_search);
                        i.putExtra("dob", dob_search);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(UserActivity.this, "Non found", Toast.LENGTH_LONG).show();
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

    public boolean validateInput() {
        filled_fields = new HashMap<>();
        EditText name_txt, lname_txt, fname_txt, mname_txt;
        name_txt = (EditText) findViewById(R.id.name);
        lname_txt = (EditText) findViewById(R.id.last_name);
        fname_txt = (EditText) findViewById(R.id.father_name);
        mname_txt = (EditText) findViewById(R.id.mother_name);

        int required = 0;

        if (name_txt.getText().toString().trim().length() != 0) {
            filled_fields.put("name", name_txt.getText().toString());
            required++;
        }
        if (lname_txt.getText().toString().trim().length() != 0) {
            filled_fields.put("lname", lname_txt.getText().toString());
            required++;
        }
        if (fname_txt.getText().toString().trim().length() != 0) {
            filled_fields.put("fname", fname_txt.getText().toString());
            required++;
        }
        if (mname_txt.getText().toString().trim().length() != 0) {
            filled_fields.put("mname", mname_txt.getText().toString());
            required++;
        }

        return (required >= 3);
    }

    public HashMap<String, String> getUserData() {
        EditText name_txt, lname_txt, fname_txt, mname_txt;
        name_txt = (EditText) findViewById(R.id.name);
        lname_txt = (EditText) findViewById(R.id.last_name);
        fname_txt = (EditText) findViewById(R.id.father_name);
        mname_txt = (EditText) findViewById(R.id.mother_name);

        String name = name_txt.getText().toString();
        String lname = lname_txt.getText().toString();
        String fname = fname_txt.getText().toString();
        String mname = mname_txt.getText().toString();

        DatePicker dob_dialog = (DatePicker) findViewById(R.id.dob);

        String dob = dob_dialog.getDayOfMonth() + "" + String.format("%02d", dob_dialog.getMonth() + 1) + "" + dob_dialog.getYear();

        RadioGroup gender_group = (RadioGroup) findViewById(R.id.gender);
        int selectedGender = gender_group.getCheckedRadioButtonId();


        String gender = (selectedGender == R.id.male) ? "male" : "female";

        HashMap<String, String> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("last_name", lname);
        userData.put("father_name", fname);
        userData.put("mother_name", mname);
        userData.put("dob", dob);
        userData.put("gender", gender);

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

    public void goToList(View v) {
        Intent i = new Intent(UserActivity.this, ListItemsActivity.class);
        i.putExtra("users", suggested_users);
        startActivity(i);
        finish();
    }

}
