package com.example.firebase_task;

public class Notes
{
    private String NoteTitle;
    private String NoteContent;
    private boolean NoteImportance;

    public Notes(String noteTitle, String noteContent, boolean noteImportance) {
        this.NoteTitle = noteTitle;
        this.NoteContent = noteContent;
        this.NoteImportance = noteImportance;
    }
    public Notes(){}

    public String getNoteTitle() {
        return NoteTitle;
    }
    public void setNoteTitle(String noteTitle) {
        NoteTitle = noteTitle;
    }
    public String getNoteContent() {
        return NoteContent;
    }
    public void setNoteContent(String noteContent) {
        NoteContent = noteContent;
    }
    public boolean isNoteImportance() {
        return NoteImportance;
    }
    public void setNoteImportance(boolean noteImportance) {
        NoteImportance = noteImportance;
    }
}
