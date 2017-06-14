package com.example.rzahab.generator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ListSuggestedActivity extends AppCompatActivity {

    private final String TAG = "ListActivity";
    DatabaseReference mDB;
    DatabaseReference mListItemRef;
    private RecyclerView mListItemsRecyclerView;
    private ListItemsAdapter mAdapter;
    private ArrayList<ListItem> myListItems;
    private FirebaseUser CurrentUser;
    Generator app;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_suggested);

        app = ((Generator) this.getApplication());
        mListItemsRecyclerView = (RecyclerView) findViewById(R.id.listItem_recycler_view);
        // mListItemsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        mListItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();

        String gender = extras.getString("gender");
        String dob = extras.getString("dob");

        TextView filterTextView = (TextView) findViewById(R.id.filterTextView);
        filterTextView.setText("Suggested " + gender + " users, born on: " + dob);

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
            case R.id.action_delete_all:
                //deleteAllListItems();
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
