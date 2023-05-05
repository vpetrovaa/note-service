package com.solvd.noteservice.service;

import com.solvd.noteservice.domain.Note;

import java.util.List;

public interface NoteService {

    boolean isExistById(Long id);

    Note create(Note note);

    List<Note> findAllByUserId(Long userId);

    List<Note> findAll();

    Note findById(Long id);

    Note update(Note note);

    void delete(Long id);

}
