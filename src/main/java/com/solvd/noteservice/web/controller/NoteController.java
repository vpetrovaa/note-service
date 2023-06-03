package com.solvd.noteservice.web.controller;

import com.solvd.noteservice.domain.Note;
import com.solvd.noteservice.service.NoteService;
import com.solvd.noteservice.web.dto.NoteDto;
import com.solvd.noteservice.web.mapper.NoteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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
    @MutationMapping
    public final NoteDto create(@RequestBody @Validated @Argument final NoteDto note) {
        Note noteMapped = noteMapper.toEntity(note);
        noteMapped = noteService.create(noteMapped);
        return noteMapper.toDto(noteMapped);
    }

    @GetMapping("/users/{userId}")
    @QueryMapping
    public final List<NoteDto> findAllByUserId(@PathVariable("userId")
                                                   @Argument final Long userId) {
        List<Note> notes = noteService.findAllByUserId(userId);
        return noteMapper.toDtoList(notes);
    }

    @GetMapping
    @QueryMapping
    public final List<NoteDto> findAll() {
        List<Note> notes = noteService.findAll();
        return noteMapper.toDtoList(notes);
    }

    @GetMapping("/{id}")
    @QueryMapping
    public final NoteDto findById(@PathVariable("id") @Argument final Long id) {
        Note note = noteService.findById(id);
        return noteMapper.toDto(note);
    }

    @GetMapping("/exists/{id}")
    @QueryMapping
    public final boolean isExistById(@PathVariable @Argument final Long id) {
        return noteService.isExistById(id);
    }

    @PutMapping
    @MutationMapping
    public final NoteDto update(@RequestBody @Validated @Argument final NoteDto note) {
        Note noteMapped = noteMapper.toEntity(note);
        noteMapped = noteService.update(noteMapped);
        return noteMapper.toDto(noteMapped);
    }

    @DeleteMapping("/{id}")
    @MutationMapping
    public final void delete(@PathVariable @Argument final Long id) {
        noteService.delete(id);
    }

}
