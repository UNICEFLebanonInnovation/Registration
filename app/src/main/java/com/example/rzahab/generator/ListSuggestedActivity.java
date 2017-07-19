package com.example.rzahab.generator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static android.text.Html.fromHtml;

public class ListSuggestedActivity extends AppCompatActivity {

    Generator app;
    private RecyclerView mListItemsRecyclerView;
    private ListItemsAdapter mAdapter;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_suggested);

        String TAG = this.getLocalClassName();
        app = ((Generator) this.getApplication());
        mListItemsRecyclerView = (RecyclerView) findViewById(R.id.listItem_recycler_view);
        mListItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String gender = (extras.containsKey("gender")) ? extras.getString("gender") : null;
            String dob = (extras.containsKey("dob")) ? extras.getString("dob") : null;

            TextView genderTextView = (TextView) findViewById(R.id.genderTextView);
            genderTextView.setText(fromHtml(getString(R.string.suggested_users)
                    + getString(R.string.start_b) + gender + getString(R.string.end_b)));

            //TextView dobTextView = (TextView) findViewById(R.id.dobTextView);
            assert dob != null;
            //dob = getString(R.string.start_b) + dob.substring(0, 2) + getString(R.string.slash) + dob.substring(2, 4) + getString(R.string.slash) + dob.substring(4) + getString(R.string.end_b);
            //dobTextView.setText(fromHtml(getString(R.string.born_on) + dob));
        }
        updateUI(app.getSuggestedUsers());

    }

    private void updateUI(ArrayList<SuggestedUser> myListItems) {
        mAdapter = new ListItemsAdapter(myListItems, this);
        mListItemsRecyclerView.setAdapter(mAdapter);
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

    public void addNew(View v) {
        startActivity(new Intent(this, NewUserActivity.class));
        finish();
    }

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

}
