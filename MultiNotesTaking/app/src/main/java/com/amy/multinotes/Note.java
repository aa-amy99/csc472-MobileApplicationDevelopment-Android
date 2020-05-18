package com.amy.multinotes;

public class Note {
    private String noteTitle;
    private String noteText;
    private long noteTimeStamp;


    public  Note(String title, String noteText, long timeStamp){

        this.noteTitle = title;
        this.noteText = noteText;
        this.noteTimeStamp = timeStamp;

    }

    public String getTitle() { return this.noteTitle; }

    public String getNoteText() { return this.noteText; }

    public long getTimeStamp() { return this.noteTimeStamp; }


    @Override
    public String toString() {
        return String.format( "Note: %s [ %s ] %d",noteTitle, noteText,noteTimeStamp);
    }




}
