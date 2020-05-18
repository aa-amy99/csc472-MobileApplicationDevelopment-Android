package com.amy.multinotes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

    private static  final String TAG = "EditActivityTag";
    private EditText noteTitle;
    private EditText noteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate: GET DATA FROM INTENT AND SET TO TEXTVIEW");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        noteTitle = findViewById(R.id.InputTitle);
        noteContent = findViewById(R.id.InputContent);
        setTitle("Multi Notes");

        Intent intents = getIntent();
        String titleIn = (intents.hasExtra("noteTitle") && intents.hasExtra("noteText")) ?
                intents.getStringExtra("noteTitle") : "";
        noteTitle.setText(titleIn);
        String textIn = (intents.hasExtra("noteTitle") && intents.hasExtra("noteText")) ?
                intents.getStringExtra("noteText") : "" ;
        noteContent.setText(textIn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        Log.d(TAG, "onCreateOptionsMenu: INFLATE SAVE MENU");
        getMenuInflater().inflate(R.menu.save_activity, menu);
        return true;
    }

    @Override
    public void onBackPressed(){

        Log.d(TAG, "onBackPressed: SAVING EDIT DATA");
        final String noteTitle = this.noteTitle.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent();
                intent.putExtra("intentTitle", EditActivity.this.noteTitle.getText().toString());
                intent.putExtra("intentContent", noteContent.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        builder.setTitle("Your note is NOT SAVED!");
        builder.setMessage(String.format("Save note '%s' ?", noteTitle));
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){

        Log.d(TAG, "onOptionsItemSelected: PUTTING DATA TO INTENT");
        Intent intent = new Intent();
        intent.putExtra("intentTitle", noteTitle.getText().toString());
        intent.putExtra("intentContent", noteContent.getText().toString());
        setResult(RESULT_OK, intent);
        finish();

        return true;
    }
}

