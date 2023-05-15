package com.solvd.noteservice;

import com.solvd.noteservice.domain.Note;

public class NoteFactory {

    public static Note generateNote() {
        return new Note(1L, "simple note", "main theme", "1.0", 1L);
    }

}
