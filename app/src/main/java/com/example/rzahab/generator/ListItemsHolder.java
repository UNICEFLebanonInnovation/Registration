package com.example.rzahab.generator;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Rzahab on 6/13/2017.
 */

public class ListItemsHolder extends RecyclerView.ViewHolder {
    public TextView nameTextView, motherNameTextView, fatherNameTextView;

    public ListItemsHolder(View itemView) {
        super(itemView);
        nameTextView = (TextView) itemView.findViewById(R.id.textview_name);
        motherNameTextView = (TextView) itemView.findViewById(R.id.textview_mother_name);
        fatherNameTextView = (TextView) itemView.findViewById(R.id.textview_father_name);
    }

    public void bindData(SuggestedUser user) {
        nameTextView.setText(user.getFullName());
        motherNameTextView.setText(user.getMotherName());
        fatherNameTextView.setText(user.getFatherName());
    }
}