package com.solvd.noteservice.service.impl;

import com.solvd.noteservice.domain.Note;
import com.solvd.noteservice.domain.exception.IllegalOperationException;
import com.solvd.noteservice.domain.exception.ResourceDoesNotExistException;
import com.solvd.noteservice.kafka.KfProducer;
import com.solvd.noteservice.kafka.event.NoteEvent;
import com.solvd.noteservice.repository.NoteRepository;
import com.solvd.noteservice.service.NoteService;
import com.solvd.noteservice.service.client.ElasticClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final ElasticClient elasticClient;
    private final RestTemplate restTemplate;
    private final KfProducer kfProducer;

    @Override
    public final boolean isExistById(final Long id) {
        return elasticClient.isExistById(id);
    }

    @Override
    @Transactional
    public final Note create(final Note note) {
        Boolean isExistByUserId = restTemplate
                .getForObject("http://user/api/v1/users/{id}",
                Boolean.class,
                note.getUserId());
        if (Boolean.FALSE.equals(isExistByUserId)) {
            throw new ResourceDoesNotExistException(
                    "There are no user with id " + note.getUserId()
            );
        }

        NoteEvent noteEvent = new NoteEvent();
        noteEvent.setType(NoteEvent.Method.POST);
        noteEvent.setId(note.getId());
        noteEvent.setDescription(note.getDescription());
        noteEvent.setTheme(note.getTheme());
        noteEvent.setTag(note.getTag());
        noteEvent.setUserId(note.getUserId());
        kfProducer.sendMessage(noteEvent);
        return noteRepository.save(note);
    }

    @Override
    public final List<Note> findAllByUserId(final Long userId) {
        return elasticClient.findAllByUserId(userId);
    }

    @Override
    public final List<Note> findAll() {
        return elasticClient.findAll();
    }

    @Override
    public final Note findById(final Long id) {
        Note note = elasticClient.findById(id)
                .orElseThrow(() -> new ResourceDoesNotExistException(
                        "There are no note with id" + id)
                );
        return note;
    }

    @Override
    public final Note update(final Note note) {
        Note noteFromDb = noteRepository.findById(note.getId()).get();
        if (!noteFromDb.getTheme().equals(note.getTheme())) {
            throw new IllegalOperationException(
                    "You cant change the theme of note"
            );
        }

        NoteEvent noteEvent = new NoteEvent();
        noteEvent.setType(NoteEvent.Method.PUT);
        noteEvent.setId(note.getId());
        noteEvent.setDescription(note.getDescription());
        noteEvent.setTheme(note.getTheme());
        noteEvent.setTag(note.getTag());
        noteEvent.setUserId(note.getUserId());
        kfProducer.sendMessage(noteEvent);
        return noteRepository.save(note);
    }

    @Override
    public final void delete(final Long id) {
        noteRepository.deleteById(id);

        NoteEvent noteEvent = new NoteEvent();
        noteEvent.setType(NoteEvent.Method.DELETE);
        noteEvent.setId(id);
        kfProducer.sendMessage(noteEvent);
    }

}
