package com.example.rzahab.generator;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Rzahab on 6/13/2017.
 */

public class ListItemsAdapter extends RecyclerView.Adapter<ListItemsHolder> {

    RecyclerView myRecyclerView;

    public ArrayList<SuggestedUser> getmListItems() {
        return mListItems;
    }

    private ArrayList<SuggestedUser> mListItems;
    private Activity currentActivity;

    public ListItemsAdapter(ArrayList<SuggestedUser> ListItems, Activity activity) {
        mListItems = ListItems;
        currentActivity = activity;
    }
    
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        myRecyclerView = recyclerView;
       // this.getItemId()
    }

    @Override
    public ListItemsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(currentActivity);
        View view = layoutInflater.inflate(R.layout.suggested_list_item, parent, false);
        view.setBackground(currentActivity.getDrawable(R.drawable.ripple));
        view.setOnClickListener(new SuggestedItemOnClickListener(myRecyclerView,currentActivity));
        return new ListItemsHolder(view);

    }

    @Override
    public void onBindViewHolder(ListItemsHolder holder, int position) {
        SuggestedUser s = mListItems.get(position);
        holder.bindData(s);
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }


    public String getItemUId(int position)
    {
        return this.mListItems.get(position).getID();
    }

}