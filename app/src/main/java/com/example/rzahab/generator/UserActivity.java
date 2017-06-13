package com.example.rzahab.generator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
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

import static android.R.attr.data;
import static android.os.Build.ID;
import static com.example.rzahab.generator.R.id.dob;
import static com.example.rzahab.generator.R.id.fill;
import static com.example.rzahab.generator.R.id.gender;
import static com.example.rzahab.generator.R.id.name;

public class UserActivity extends AppCompatActivity implements Serializable {

    private Button signOutButton;
    private TextView helloUserText;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    private FirebaseUser CurrentUser;
    private String TAG;
    Map<String, String> filled_fields;
    SuggestionLibrary sl;
    HashMap<String, HashMap<String, String>> suggested_users;
    ArrayList<String> user_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        helloUserText = (TextView) findViewById(R.id.hello_label);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        TAG = this.getLocalClassName().toString();
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
        sl = new SuggestionLibrary();
        //getData();
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

        DatePicker dob_dialog = (DatePicker) findViewById(dob);

        String dob = dob_dialog.getDayOfMonth() + "" + String.format("%02d", dob_dialog.getMonth() + 1) + "" + dob_dialog.getYear();

        RadioGroup gender_group = (RadioGroup) findViewById(gender);
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

    public void search(View v) {
        if (validateInput()) {


            HashMap<String, String> userData = getUserData();
            String gender_search = userData.get("gender");
            String dob_search = userData.get("dob");

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            suggested_users = new HashMap<>();
            user_ids = new ArrayList<>();

            DatabaseReference myRef = database.getReference("users")
                    .child("gender").child(gender_search).child("22071989"/**dob_search**/);

            final HashMap<String, HashMap<String, String>> users = new HashMap<>();


            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        HashMap<String, String> currentUser = new HashMap<>();
                        String ID = postSnapshot.getKey();

                        Log.d(TAG, "==== ID: " + ID + "====");
                        for (DataSnapshot userAttribute : postSnapshot.getChildren()) {
                            //Log.d(TAG, "=======" + userAttribute.getKey() + " : " + userAttribute.getValue());
                            currentUser.put(userAttribute.getKey() + "", userAttribute.getValue() + "");
                        }
                        double rate = getDifferenceRate(currentUser);
                        currentUser.put(("rate"), rate + "");
                        Log.d(TAG, "Rate : " + ID + " with: " + rate);

                        if (rate < 50) {
                            suggested_users.put(ID, currentUser);
                            Log.d(TAG, "suggested : " + ID + " with: " + rate);
                            user_ids.add(ID);
                        }
                        users.put(ID, currentUser);
                        Log.d(TAG, "==== ====");

                    }
                    Intent i = new Intent(UserActivity.this, ListItemsActivity.class);
                    i.putExtra("user_ids", user_ids);
                    startActivity(i);
                    finish();
                }


                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        } else {
            Toast.makeText(this, "Make sure to provide at least 3 out of 4 names", Toast.LENGTH_LONG).show();
        }


    }

    public double getDifferenceRate(HashMap<String, String> currentUser) {


        double rate = 0;
        int different_fields = 0;

        for (Map.Entry<String, String> entry : filled_fields.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();
            String equiv_key = key + "_equiv";

            String user_val = currentUser.containsKey(equiv_key) ? currentUser.get(equiv_key) : currentUser.get(key);

            Double current_rate = sl.getDifference(value, user_val);
            Log.d(TAG, key + ", " + equiv_key + " : (" + value + "==" + user_val + ") = " + current_rate);

            if (current_rate > 0)
                different_fields++;
            if (current_rate > 50)
                return current_rate;

            rate = rate + current_rate;
        }

        return (different_fields == 0) ? rate : rate / different_fields;
    }

    public void getData() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //database.getReference("listItem").keepSynced(true);
        DatabaseReference myRef = database.getReference("listItem").child("gender").child("female").child("22071989");
        Log.d(TAG, "Ref is: " + myRef.getRef());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "Count is: " + dataSnapshot.getChildrenCount());

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String ID = postSnapshot.getKey();
                    Log.d(TAG, "ID: " + ID);
                    for (DataSnapshot userAttribute : postSnapshot.getChildren()) {
                        Log.d(TAG, "=======" + userAttribute.getKey() + " : " + userAttribute.getValue());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

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
