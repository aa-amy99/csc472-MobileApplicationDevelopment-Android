package com.amy.knowyourgovernment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OfficerAdapter extends RecyclerView.Adapter<OfficerViewHolder> {

    private static final String TAG = "OFFICER_ADAPTER";
    private ArrayList<Officer> officerList;
    private MainActivity mainActivity;

    OfficerAdapter(ArrayList<Officer> list, MainActivity mainActivity) {
        officerList = list;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public OfficerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder: to create new officer item");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_official_items, parent, false);

        itemView.setOnClickListener( mainActivity );
        itemView.setOnLongClickListener(mainActivity);
        return new OfficerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficerViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: to set the data shown on RecycleView");

        Officer selectedOfficer = officerList.get(position);
        holder.roleView.setText(selectedOfficer.getRole());
        holder.nameView.setText( selectedOfficer.getName() );
        holder.partyView.setText( String.format( "(%s)", selectedOfficer.getParty()) );
    }

    @Override
    public int getItemCount() {
        return officerList.size();
    }
}
