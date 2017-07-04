package com.example.rzahab.generator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class NewUserActivity extends AppCompatActivity {

    Generator app;
    HashMap<String, String> userData;
    EditText first_name_txt, last_name_txt, father_name_txt, mother_name_txt, dob_txt;
    String TAG;
    Activity currentActivity;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        app = (Generator) this.getApplication();
        TAG = this.getClass().getSimpleName();
        userData = app.getUserData();
        currentActivity = this;
        populateData();
    }

    public boolean validateInput() {

        EditText first_name_txt, last_name_txt, father_name_txt, mother_name_txt;
        first_name_txt = (EditText) findViewById(R.id.first_name);
        last_name_txt = (EditText) findViewById(R.id.last_name);
        father_name_txt = (EditText) findViewById(R.id.father_name);
        mother_name_txt = (EditText) findViewById(R.id.mother_name);

        if (first_name_txt.getText().toString().trim().length() != 0) {
            userData.put("first_name", first_name_txt.getText().toString());
        } else
            return false;
        if (last_name_txt.getText().toString().trim().length() != 0) {
            userData.put("last_name", last_name_txt.getText().toString());
        } else
            return false;
        if (father_name_txt.getText().toString().trim().length() != 0) {
            userData.put("father_name", father_name_txt.getText().toString());
        } else
            return false;
        if (mother_name_txt.getText().toString().trim().length() != 0) {
            userData.put("mother_name", mother_name_txt.getText().toString());
        } else
            return false;

        return true;
    }

    public void populateData() {
        first_name_txt = (EditText) findViewById(R.id.first_name);
        last_name_txt = (EditText) findViewById(R.id.last_name);
        father_name_txt = (EditText) findViewById(R.id.father_name);
        mother_name_txt = (EditText) findViewById(R.id.mother_name);
        dob_txt = (EditText) findViewById(R.id.dob);

        first_name_txt.setText(userData.get("first_name"));
        last_name_txt.setText(userData.get("last_name"));
        father_name_txt.setText(userData.get("father_name"));
        mother_name_txt.setText(userData.get("mother_name"));
        String dob = userData.get("dob");
        dob = dob.substring(0, 2) + " / " + dob.substring(2, 4) + " / " + dob.substring(4);
        dob_txt.setText(dob);

        RadioButton checked_radio;
        Log.d(TAG, "Gender: " + userData.get("gender"));
        if (userData.get("gender").equals("female")) {
            checked_radio = (RadioButton) findViewById(R.id.female);
        } else {
            checked_radio = (RadioButton) findViewById(R.id.male);
        }
        checked_radio.setChecked(true);
    }

    public void add(View v) {
        if (!validateInput()) {
            Toast.makeText(this, R.string.error_all_info, Toast.LENGTH_LONG).show();
        } else {
            String first_name_equiv, last_name_equiv, father_name_equiv, mother_name_equiv;

            SuggestionLibrary sl = new SuggestionLibrary();

            if (sl.requireTrans(userData.get("first_name"))) {
                first_name_equiv = sl.transliterate(userData.get("first_name"));
                userData.put("first_name_equiv", first_name_equiv);
            }
            if (sl.requireTrans(userData.get("last_name"))) {
                last_name_equiv = sl.transliterate(userData.get("last_name"));
                userData.put("last_name_equiv", last_name_equiv);
            }
            if (sl.requireTrans(userData.get("father_name"))) {
                father_name_equiv = sl.transliterate(userData.get("father_name"));
                userData.put("father_name_equiv", father_name_equiv);
            }
            if (sl.requireTrans(userData.get("mother_name"))) {
                mother_name_equiv = sl.transliterate(userData.get("mother_name"));
                userData.put("mother_name_equiv", mother_name_equiv);
            }
            generateID();
        }

    }

    public void generateID() {
        User u = new User(userData);
        IDGen idGen = new IDGen(u);
        final String UID = idGen.generateID();
        u.setUid(UID);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users").child(userData.get("gender"))
                .child(userData.get("dob")).child(UID);

        usersRef.setValue(u, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    //app.moveToKobo(currentActivity, UID);
                    Intent previewIntent = new Intent(currentActivity, PreviewActivity.class);
                    previewIntent.putExtra("UID", "" + UID);
                    startActivity(previewIntent);
                    finish();
                }
            }
        });

        //GsonRequest g = new GsonRequest(currentActivity, "populateUI");
        //g.post(u.asPost());
    }

    /*
    Legacy : was used when we were calling the Generator ID API

    public void populateUI(String jsonResponse) {
        Gson gson = new GsonBuilder().create();
        final HashID hashID = gson.fromJson(jsonResponse, HashID.class);

        final String ID = hashID.getHash();
        Log.d(TAG, "Generated ID: " + ID);

        User u = new User(userData);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users").child(userData.get("gender"))
                .child(userData.get("dob")).child(ID);

        usersRef.setValue(u, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println(getString(R.string.error_saving) + databaseError.getMessage());
                } else {
                    app.moveToKobo(currentActivity, ID);
                }
            }
        });
    }
    */


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            startActivity(new Intent(this, SearchUserActivity.class));
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.back_search, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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
