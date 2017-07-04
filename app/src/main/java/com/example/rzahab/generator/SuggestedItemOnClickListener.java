package com.example.rzahab.generator;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Rzahab on 6/14/2017.
 */

public class SuggestedItemOnClickListener implements View.OnClickListener {

    private RecyclerView recyclerView;
    private Activity currentActivity;
    private ArrayList<SuggestedUser> itemsList;

    SuggestedItemOnClickListener(RecyclerView recyclerView, Activity currentActivity) {
        this.recyclerView = recyclerView;
        this.currentActivity = currentActivity;
        itemsList = ( (ListItemsAdapter)this.getRecyclerView().getAdapter()).getmListItems();
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public void onClick(View v) {
        int itemPosition = recyclerView.indexOfChild(v);
        String UID = itemsList.get((itemPosition)).getID();

        Log.d("Listener", "UID: " + UID);

        ((Generator) currentActivity.getApplication()).moveToKobo(currentActivity,UID);
    }


}