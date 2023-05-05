package com.solvd.noteservice.web.controller;

import com.solvd.noteservice.domain.Note;
import com.solvd.noteservice.service.NoteService;
import com.solvd.noteservice.web.dto.NoteDto;
import com.solvd.noteservice.web.mapper.NoteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;
    private final NoteMapper noteMapper;

    @PostMapping
    public NoteDto create(@RequestBody @Validated NoteDto noteDto) {
        Note note = noteMapper.toEntity(noteDto);
        note = noteService.create(note);
        return noteMapper.toDto(note);
    }

    @GetMapping("/users/{userId}")
    public List<NoteDto> findAllByUserId(@PathVariable("userId") Long userId) {
        List<Note> notes = noteService.findAllByUserId(userId);
        return noteMapper.toDtoList(notes);
    }

    @GetMapping
    public List<NoteDto> findAll() {
        List<Note> notes = noteService.findAll();
        return noteMapper.toDtoList(notes);
    }

    @GetMapping("/{id}")
    public NoteDto findById(@PathVariable("id") Long id) {
        Note note = noteService.findById(id);
        return noteMapper.toDto(note);
    }

    @GetMapping("/exists/{id}")
    public boolean isExistById(@PathVariable Long id) {
        return noteService.isExistById(id);
    }

    @PutMapping
    public NoteDto update(@RequestBody @Validated NoteDto noteDto) {
        Note note = noteMapper.toEntity(noteDto);
        note = noteService.update(note);
        return noteMapper.toDto(note);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        noteService.delete(id);
    }

}
