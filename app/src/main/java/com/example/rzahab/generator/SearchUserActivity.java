package com.example.rzahab.generator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
    private String TAG;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        TAG = this.getLocalClassName();
        app = ((Generator) this.getApplication());
        app.setUserData(null);
        app.authenticateUser(this);
    }

    public void showAll(View v) {
        Intent i = new Intent(SearchUserActivity.this, ListUsersActivity.class);
        startActivity(i);
        finish();
    }

    public void search(View v) {
        if (!validateInput()) {
            Toast.makeText(this, "Make sure to provide at least 3 out of 4 names", Toast.LENGTH_LONG).show();
        } else {

            HashMap<String, String> userData = getUserData();
            final String gender_search = userData.get("gender");
            final String dob_search = userData.get("dob");
            Log.d(TAG, "Searching dob: " + dob_search);

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
                        Toast.makeText(SearchUserActivity.this, R.string.non_found, Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, R.string.exit_back, Toast.LENGTH_SHORT).show();

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

        String dob = String.format("%02d", dob_dialog.getDayOfMonth()) + "" + month + "" + dob_dialog.getYear();

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    //sign out method
    public void signOut(View v) {
        app.signOutAuth();
    }


    @Override
    public void onStart() {
        super.onStart();
        app.startAuth();
    }

    @Override
    public void onStop() {
        super.onStop();
        app.endAuth();
    }

    public void addNew() {
        startActivity(new Intent(this, NewUserActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            /*case R.id.action_delete_all:
                //deleteAllListItems();
                break;
                */
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.action_home:
                startActivity(new Intent(this, SearchUserActivity.class));
                finish();
                break;
            case R.id.action_language:
                startActivity(new Intent(this, LanguageActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
