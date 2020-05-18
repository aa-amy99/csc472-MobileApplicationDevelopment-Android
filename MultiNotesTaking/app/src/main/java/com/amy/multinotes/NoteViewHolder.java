package com.amy.multinotes;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class NoteViewHolder extends RecyclerView.ViewHolder {
    TextView NoteTitle;
    TextView NoteContent;
    TextView NoteTimeStamp;


    NoteViewHolder(View view){
        super(view);
        NoteTitle = view.findViewById(R.id.titleItem);
        NoteContent = view.findViewById(R.id.InputContent);
        NoteTimeStamp = view.findViewById(R.id.timeItem);


    }
}
