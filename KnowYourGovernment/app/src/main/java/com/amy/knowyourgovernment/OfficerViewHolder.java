package com.amy.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class OfficerViewHolder extends RecyclerView.ViewHolder {

    TextView roleView;
    TextView nameView;
    TextView partyView;

    OfficerViewHolder  (View view) {
        super(view);
        roleView = view.findViewById(R.id.role);
        nameView = view.findViewById(R.id.name);
        partyView = view.findViewById(R.id.party);


    }
}
