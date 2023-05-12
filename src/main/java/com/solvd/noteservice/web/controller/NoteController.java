package com.solvd.noteservice.web.controller;

import com.solvd.noteservice.domain.Note;
import com.solvd.noteservice.service.NoteService;
import com.solvd.noteservice.web.dto.NoteDto;
import com.solvd.noteservice.web.mapper.NoteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;
    private final NoteMapper noteMapper;

    @PostMapping
    public final NoteDto create(@RequestBody @Validated final NoteDto noteDto) {
        Note note = noteMapper.toEntity(noteDto);
        note = noteService.create(note);
        return noteMapper.toDto(note);
    }

    @GetMapping("/users/{userId}")
    public final List<NoteDto> findAllByUserId(@PathVariable("userId")
                                                   final Long userId) {
        List<Note> notes = noteService.findAllByUserId(userId);
        return noteMapper.toDtoList(notes);
    }

    @GetMapping
    public final List<NoteDto> findAll() {
        List<Note> notes = noteService.findAll();
        return noteMapper.toDtoList(notes);
    }

    @GetMapping("/{id}")
    public final NoteDto findById(@PathVariable("id") final Long id) {
        Note note = noteService.findById(id);
        return noteMapper.toDto(note);
    }

    @GetMapping("/exists/{id}")
    public final boolean isExistById(@PathVariable final Long id) {
        return noteService.isExistById(id);
    }

    @PutMapping
    public final NoteDto update(@RequestBody @Validated final NoteDto noteDto) {
        Note note = noteMapper.toEntity(noteDto);
        note = noteService.update(note);
        return noteMapper.toDto(note);
    }

    @DeleteMapping("/{id}")
    public final void delete(@PathVariable final Long id) {
        noteService.delete(id);
    }

}
