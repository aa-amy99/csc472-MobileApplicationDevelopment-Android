package com.amy.multinotes;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.util.Log;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.view.LayoutInflater;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private static final String TAG = "NoteAdapterTag";
    private ArrayList<Note> myNoteList;
    private MainActivity myMainActivity;

    NoteAdapter(ArrayList<Note> noteList, MainActivity mainactivity){
        this.myNoteList = noteList;
        this.myMainActivity = mainactivity;
    }


    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder noteHolder, int position) {
        Log.d(TAG, "onBindViewHolder: SETTING DATA TO VIEW HOLDER ");
        Note selectedNote = myNoteList.get(position);
        String originalText = selectedNote.getNoteText();
        int textLen = originalText.length();
        //set Title
        noteHolder.NoteTitle.setText(selectedNote.getTitle());

        //set Text (show only first 80 words)
        String showedText =  textLen  < 80 ? originalText : originalText.substring(0, 79).concat("...");
        noteHolder.NoteContent.setText(showedText);

        //set timeStamp
        Log.d(TAG, "onBindViewHolder: SETTING timestamp");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EE MMM d, HH:mm aaa");
        noteHolder.NoteTimeStamp.setText(dateFormatter.format(new Date(selectedNote.getTimeStamp())));

    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType){

        Log.d(TAG, "onCreateViewHolder: CREATING NEW HOLDER");
        //inflate the layout
        //pass the parent context (NoteViewHolder)
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        itemView.setOnClickListener(myMainActivity);
        itemView.setOnLongClickListener(myMainActivity);

        return new NoteViewHolder(itemView);

    }

    @Override
    public int getItemCount() {
        return myNoteList.size();
    }

}




