package com.solvd.noteservice.repository;

import com.solvd.noteservice.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    boolean existsById(Long id);

    List<Note> findAllByUserId(Long userId);

}
