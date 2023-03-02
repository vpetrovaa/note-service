package com.solvd.noteservice.service;

import com.solvd.noteservice.domain.Note;

import java.util.List;

public interface NoteService {

    Note create(Note note);

    List<Note> findAllByUserId(Long userId);

}
