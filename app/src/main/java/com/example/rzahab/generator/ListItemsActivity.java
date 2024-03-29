package com.example.rzahab.generator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListItemsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_list_items);

        Bundle extras = getIntent().getExtras();
        app = ((Generator) this.getApplication());
        if (extras != null) {
            String userName = extras.getString("name");
            ArrayList<String> user_ids = extras.getStringArrayList("user_ids");

        }

        mDB = FirebaseDatabase.getInstance().getReference();
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mListItemRef = mDB.child("listItem");
        myListItems = new ArrayList<>();
        mListItemsRecyclerView = (RecyclerView) findViewById(R.id.listItem_recycler_view);
        mListItemsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        mListItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //updateUI();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewListItem();
                getSuggested();
            }
        });

        mListItemRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG + "Added", dataSnapshot.getValue(ListItem.class).toString());
                fetchData(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG + "Changed", dataSnapshot.getValue(ListItem.class).toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG + "Removed", dataSnapshot.getValue(ListItem.class).toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG + "Moved", dataSnapshot.getValue(ListItem.class).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG + "Cancelled", databaseError.toString());
            }
        });
    }

    private void getSuggested() {
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
                deleteAllListItems();
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createNewListItem() {
        // Create new List Item  at /listItem
        final String key = FirebaseDatabase.getInstance().getReference().child("users").push().getKey();
        LayoutInflater li = LayoutInflater.from(this);
        View getListItemView = li.inflate(R.layout.dialog_get_list_item, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(getListItemView);

        final EditText userInput = (EditText) getListItemView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text

                        String listItemText = userInput.getText().toString();
                        ListItem listItem = new ListItem(listItemText, CurrentUser);
                        Map<String, Object> listItemValues = listItem.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/listItem/" + key, listItemValues);
                        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);

                    }
                }).create()
                .show();

    }

    public void deleteAllListItems() {
        FirebaseDatabase.getInstance().getReference().child("listItem").removeValue();
        myListItems.clear();
        mAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Items Deleted Successfully", Toast.LENGTH_SHORT).show();
    }

    //called after an item is added
    private void fetchData(DataSnapshot dataSnapshot) {
        ListItem listItem = dataSnapshot.getValue(ListItem.class);
        myListItems.add(listItem);
        updateUI(app.getSuggestedUsers());
    }

    private void updateUI(ArrayList<SuggestedUser> myListItems) {
        mAdapter = new ListItemsAdapter(myListItems, this);
        mListItemsRecyclerView.setAdapter(mAdapter);
    }


}
