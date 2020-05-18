package com.amy.multinotes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,View.OnLongClickListener {

    private static  final String TAG = "MainActivityTag";
    private static final int CODE_FOR_NOTE_ACTIVITY = 1033;
    private RecyclerView noteRecycler;
    private NoteAdapter noteAdapter;
    private  ArrayList<Note> noteArrayList = new ArrayList<Note>();

    @Override
    protected void onCreate (Bundle savedInstanceState){
        Log.d(TAG, "onCreate: SET TEXTVIEW");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noteRecycler = findViewById(R.id.recycler);
        noteAdapter = new NoteAdapter(noteArrayList, this);
        noteRecycler.setAdapter(noteAdapter);
        noteRecycler.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "onCreate: READ JSON FILE");
        try{
            InputStream inFD = getApplicationContext().openFileInput("Notes.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inFD, StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String readInStr;
            while((readInStr = reader.readLine()) != null){ builder.append(readInStr); }
            reader.close();

            JSONArray jArray = new JSONArray(builder.toString());
            int arrLen = jArray.length();
            for(int i = 0; i < arrLen; i++){
                JSONObject jsonObject = (JSONObject)jArray.get(i);
                String titleStr = jsonObject.getString("JTitle");
                String textStr = jsonObject.getString("JText");
                long timestamp = jsonObject.getLong("JTimestamp");
                Note newNote = new Note(titleStr, textStr, timestamp);
                noteArrayList.add(newNote );
            }
        } catch (Exception err) { err.printStackTrace(); }

        setTitle(String.format("Multi Notes [%d]", noteArrayList.size()));
    }

    protected void onPause(){
        Log.d(TAG, "onPause : WRITE NOTE TO JSON FILE");
        super.onPause();
        try{
            FileOutputStream file = getApplicationContext().openFileOutput("Notes.json", Context.MODE_PRIVATE);
            JSONArray jArray = new JSONArray();
            for(Note noteItem : noteArrayList){
                try{
                    JSONObject NoteJson = new JSONObject();
                    NoteJson.put("JTitle", noteItem.getTitle());
                    NoteJson.put("JText", noteItem.getNoteText());
                    NoteJson.put("JTimestamp", noteItem.getTimeStamp());
                    jArray.put(NoteJson);
                }
                catch (JSONException err){ err.printStackTrace(); }
            }
            String jContent = jArray.toString();
            file.write(jContent.getBytes());
            file.close();
        }
        catch(IOException err) { err.printStackTrace();}
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        Log.d(TAG, "onCreate: INFLATE MAIN MENU");
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        Log.d(TAG, "onOptionsItemSelected: SELECT INFO/ADD MENU");
        int choice = item.getItemId();
        switch (choice) {
            case R.id.AboutInfo:
                Intent infoIntent = new Intent(this, AboutActivity.class);
                startActivity(infoIntent);
                break;

            case R.id.AddNewNote:
                Intent addIntent = new Intent(this, EditActivity.class);
                startActivityForResult(addIntent, CODE_FOR_NOTE_ACTIVITY);
                break;
        }
        return true;
    }


    @Override
    public void onClick (View v) {//UPDATE FROM Edit Activity (EDIT)
        Log.d(TAG, "onClick : TO EDIT & UPDATE NOTE ON MAIN PAGE");
        int childNote = noteRecycler.getChildAdapterPosition(v);
        Note selectedNote = noteArrayList.get(childNote);

        Intent updateIntent = new Intent(this, EditActivity.class);
        updateIntent.putExtra("noteTitle", selectedNote.getTitle());
        updateIntent.putExtra("noteText", selectedNote.getNoteText());
        setResult(RESULT_OK, updateIntent);

        startActivityForResult(updateIntent, CODE_FOR_NOTE_ACTIVITY);
        noteArrayList.remove(childNote);
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onLongClick(View v){//UPDATE FROM Edit Activity (DELETE)

        Log.d(TAG, "onClick : TO DELETE & UPDATE NOTE ON MAIN PAGE");
        int childNote = noteRecycler.getChildAdapterPosition(v);
        final Note selectedNote = noteArrayList.get(childNote);
        final String title = selectedNote.getTitle();

        Log.d(TAG, "onClick : ASK DIALOG BEFORE DELETETION");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) { }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                for (int i = 0; i < noteArrayList.size(); i++) {
                    if (title.equals(noteArrayList.get(i).getTitle())) {
                        String delete = noteArrayList.get(i).getTitle();
                        noteArrayList.remove(i);
                        noteAdapter.notifyDataSetChanged();
                    }
                }
                setTitle(String.format("Multi Notes [%d]", noteArrayList.size()));
            }
        });

        builder.setMessage(String.format( "Delete Note '%s'?", title ));
        AlertDialog alert = builder.create();
        alert.show();
        return  true;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed : BACKPRESSED FROM MAIN PAGE");
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent){

        Log.d(TAG, "onActivityResult : SHOW NEWLY ADDED NOTE ON MAIN PAGE");
        if(resultCode == RESULT_OK && requestCode == CODE_FOR_NOTE_ACTIVITY){
                String title = intent.getStringExtra("intentTitle");
                String text = intent.getStringExtra("intentContent");
                long time = System.currentTimeMillis();
                Note note = new Note(title,text,time);
                noteArrayList.add(0, note);
                noteAdapter.notifyDataSetChanged();

                boolean isEmpty = title.equals("") ? true : false;
                if(isEmpty){
                    Log.d(TAG, "onActivityResult : UNTITLED NOTE NOT ADDED");
                    noteArrayList.remove(note);
                    Toast.makeText(this,"Untitled Note is NOT SAVED, PUT <Title> To Save Note!", Toast.LENGTH_LONG).show();
                }
                setTitle(String.format("Multi Notes [%d]", noteArrayList.size()));
        }

        super.onActivityResult(requestCode,resultCode,intent);
    }

}
