package com.solvd.noteservice.service.impl;

import com.solvd.noteservice.domain.Note;
import com.solvd.noteservice.domain.exception.IllegalOperationException;
import com.solvd.noteservice.domain.exception.ResourceDoesNotExistException;
import com.solvd.noteservice.repository.NoteRepository;
import com.solvd.noteservice.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final RestTemplate restTemplate;

    @Override
    public boolean isExistById(Long id) {
        return noteRepository.existsById(id);
    }

    @Override
    @Transactional
    public Note create(Note note) {
        Boolean isExistByUserId = restTemplate.getForObject("http://user/api/v1/users/{id}",
                Boolean.class,
                note.getUserId());
        if (Boolean.FALSE.equals(isExistByUserId)) {
            throw new ResourceDoesNotExistException("There are no user with id " + note.getUserId());
        }
        noteRepository.save(note);
        return note;
    }

    @Override
    public List<Note> findAllByUserId(Long userId) {
        return noteRepository.findAllByUserId(userId);
    }

    @Override
    public List<Note> findAll() {
        return noteRepository.findAll();
    }

    @Override
    public Note findById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceDoesNotExistException("There are no note with id" + id));
        return note;
    }

    @Override
    public Note update(Note note) {
        Note noteFromDb = noteRepository.findById(note.getId()).get();
        if (!noteFromDb.getTheme().equals(note.getTheme())) {
            throw new IllegalOperationException("You cant change the theme of note");
        }
        return noteRepository.save(note);
    }

    @Override
    public void delete(Long id) {
        noteRepository.deleteById(id);
    }

}
