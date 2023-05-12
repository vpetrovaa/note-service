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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    @Value("${template.host}")
    private String templateHost;
    private final NoteRepository noteRepository;
    private final ElasticClient elasticClient;
    private final RestTemplate restTemplate;
    private final KfProducer kfProducer;

    @Override
    public boolean isExistById(final Long id) {
        return elasticClient.isExistById(id);
    }

    @Override
    @Transactional
    public Note create(Note note) {
        Boolean isExistByUserId = restTemplate
                .getForObject("http://"+ templateHost + "/api/v1/users/{id}",
                Boolean.class,
                note.getUserId());
        if (Boolean.FALSE.equals(isExistByUserId)) {
            throw new ResourceDoesNotExistException(
                    "There are no user with id " + note.getUserId()
            );
        }
        note = noteRepository.save(note);
        NoteEvent noteEvent = new NoteEvent();
        noteEvent.setType(NoteEvent.Method.POST);
        noteEvent.setId(note.getId());
        noteEvent.setDescription(note.getDescription());
        noteEvent.setTheme(note.getTheme());
        noteEvent.setTag(note.getTag());
        noteEvent.setUserId(note.getUserId());
        kfProducer.sendMessage(noteEvent);
        return note;
    }

    @Override
    public List<Note> findAllByUserId(final Long userId) {
        return elasticClient.findAllByUserId(userId);
    }

    @Override
    public List<Note> findAll() {
        return elasticClient.findAll();
    }

    @Override
    public Note findById(final Long id) {
        Note note = elasticClient.findById(id)
                .orElseThrow(() -> new ResourceDoesNotExistException(
                        "There are no note with id" + id)
                );
        return note;
    }

    @Override
    public Note update(Note note) {
        Note noteFromDb = noteRepository.findById(note.getId()).get();
        if (!noteFromDb.getTheme().equals(note.getTheme())) {
            throw new IllegalOperationException(
                    "You cant change the theme of note"
            );
        }
        note = noteRepository.save(note);
        NoteEvent noteEvent = new NoteEvent();
        noteEvent.setType(NoteEvent.Method.PUT);
        noteEvent.setId(note.getId());
        noteEvent.setDescription(note.getDescription());
        noteEvent.setTheme(note.getTheme());
        noteEvent.setTag(note.getTag());
        noteEvent.setUserId(note.getUserId());
        kfProducer.sendMessage(noteEvent);
        return note;
    }

    @Override
    public void delete(final Long id) {
        noteRepository.deleteById(id);
        NoteEvent noteEvent = new NoteEvent();
        noteEvent.setType(NoteEvent.Method.DELETE);
        noteEvent.setId(id);
        kfProducer.sendMessage(noteEvent);
    }

}
