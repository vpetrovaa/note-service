package com.solvd.noteservice;

import com.solvd.noteservice.domain.Note;
import com.solvd.noteservice.domain.exception.IllegalOperationException;
import com.solvd.noteservice.domain.exception.ResourceDoesNotExistException;
import com.solvd.noteservice.kafka.KfProducer;
import com.solvd.noteservice.repository.NoteRepository;
import com.solvd.noteservice.service.NoteService;
import com.solvd.noteservice.service.client.ElasticClient;
import com.solvd.noteservice.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    private NoteService noteService;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private ElasticClient elasticClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private KfProducer kfProducer;

    @BeforeEach
    void setUp(){
        noteService = new NoteServiceImpl(noteRepository,
                elasticClient, restTemplate, kfProducer);
    }

    @Test
    void verifyExistsByIdPassedTest() {
        Long noteId = 1L;
        Mockito.when(elasticClient.isExistById(Mockito.anyLong())).thenReturn(false);
        boolean result = noteService.isExistById(noteId);
        Assertions.assertEquals(false, result, "Assert that method returns false");
        Mockito.verify(elasticClient, Mockito.times(1)).isExistById(noteId);
    }

    @Test
    void verifyCreatePassedTest() {
        Note note = NoteFactory.generateNote();
        Long noteId = 1L;
        Mockito.when(restTemplate.getForObject(
                Mockito.anyString(), Mockito.any(), Mockito.anyLong())
        ).thenReturn(true);
        Mockito.when(noteRepository.save(Mockito.any())).thenReturn(note);
        note = noteService.create(note);
        Assertions.assertEquals(noteId, note.getId(), "Assert that notes id are equals");
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(Mockito.anyString(), Mockito.any(), Mockito.anyLong());
        Mockito.verify(noteRepository, Mockito.times(1)).save(note);
        Mockito.verify(kfProducer, Mockito.times(1))
                .sendMessage(Mockito.any());
    }

    @Test
    void verifyCreateFailedTest() {
        Note note = NoteFactory.generateNote();
        Mockito.when(restTemplate.getForObject(
                Mockito.anyString(), Mockito.any(), Mockito.anyLong())
        ).thenReturn(false);
        Assertions.assertThrows(ResourceDoesNotExistException.class, ()-> noteService.create(note),
                "Assert ResourceAlreadyExistsException");
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(Mockito.anyString(), Mockito.any(), Mockito.anyLong());
    }

    @Test
    void verifyFindAllByUserIdTest() {
        Note note = NoteFactory.generateNote();
        List<Note> notes = new ArrayList<>(List.of(note));
        Long userId = 1L;
        Mockito.when(elasticClient.findAllByUserId(Mockito.anyLong())).thenReturn(notes);
        List<Note> notesFounded = noteService.findAllByUserId(userId);
        Assertions.assertEquals(notes, notesFounded, "Assert that notes and notesFounded are equals");
        Mockito.verify(elasticClient, Mockito.times(1)).findAllByUserId(userId);
    }

    @Test
    void verifyFindAllTest() {
        Note note = NoteFactory.generateNote();
        List<Note> notes = new ArrayList<>(List.of(note));
        Mockito.when(elasticClient.findAll()).thenReturn(notes);
        List<Note> notesFounded = noteService.findAll();
        Assertions.assertEquals(notes, notesFounded, "Assert that notes and notesFounded are equals");
        Mockito.verify(elasticClient, Mockito.times(1)).findAll();
    }


    @Test
    void verifyFindByIdPassedTest() {
        Note note = NoteFactory.generateNote();
        Long noteId = 1L;
        Mockito.when(elasticClient.findById(Mockito.anyLong())).thenReturn(Optional.of(note));
        Note noteFounded = noteService.findById(noteId);
        Assertions.assertEquals(noteFounded, noteFounded, "Assert that note and noteFounded are equals");
        Mockito.verify(elasticClient, Mockito.times(1)).findById(noteId);
    }

    @Test
    void verifyFindByIdFailedTest() {
        Long noteId = 1L;
        Mockito.when(elasticClient.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceDoesNotExistException.class, () -> noteService.findById(noteId),
                "Assert ResourceDoesNotExistException");
        Mockito.verify(elasticClient, Mockito.times(1)).findById(noteId);
    }

    @Test
    void verifyUpdatePassedTest() {
        Note note = NoteFactory.generateNote();
        Long noteId = 1L;
        String newDescription = "new description";
        note.setDescription(newDescription);
        Mockito.when(noteRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(note));
        Mockito.when(noteRepository.save(Mockito.any())).thenReturn(note);
        Note noteUpdated = noteService.update(note);
        Assertions.assertEquals(note, noteUpdated, "Assert that notes are equals");
        Mockito.verify(noteRepository, Mockito.times(1)).findById(noteId);
        Mockito.verify(noteRepository, Mockito.times(1)).save(note);
        Mockito.verify(kfProducer, Mockito.times(1)).sendMessage(Mockito.any());
    }

    @Test
    void verifyUpdateFailedTest() {
        Note note = NoteFactory.generateNote();
        Long noteId = 1L;
        Note updatedNote = NoteFactory.generateNote();
        String themeForMismatch = "new description";
        updatedNote.setTheme(themeForMismatch);
        Mockito.when(noteRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(updatedNote));
        Assertions.assertThrows(IllegalOperationException.class, () -> noteService.update(note),
                "Assert ResourceDoesNotExistException");
        Mockito.verify(noteRepository, Mockito.times(1)).findById(noteId);
    }

    @Test
    void verifyDeleteTest() {
        Long noteId = 1L;
        noteService.delete(noteId);
        Mockito.verify(noteRepository, Mockito.times(1)).deleteById(noteId);
        Mockito.verify(kfProducer, Mockito.times(1)).sendMessage(Mockito.any());
    }

}
