package com.solvd.noteservice;

import com.solvd.noteservice.domain.Note;
import com.solvd.noteservice.domain.exception.IllegalOperationException;
import com.solvd.noteservice.domain.exception.ResourceDoesNotExistException;
import com.solvd.noteservice.kafka.KfProducer;
import com.solvd.noteservice.repository.NoteRepository;
import com.solvd.noteservice.service.NoteService;
import com.solvd.noteservice.service.client.ElasticClient;
import com.solvd.noteservice.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doAnswer;

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
        when(elasticClient.isExistById(anyLong())).thenReturn(false);
        boolean result = noteService.isExistById(noteId);
        assertEquals(false, result, "Assert that method returns false");
        verify(elasticClient, times(1)).isExistById(noteId);
    }

    @Test
    void verifyCreatePassedTest() {
        Note note = NoteFactory.generateNote();
        Long noteId = 1L;
        note.setId(null);
        when(restTemplate.getForObject(anyString(), any(), anyLong())).thenReturn(true);
        doAnswer(invocation -> {
            Note noteCreated = invocation.getArgument(0);
            noteCreated.setId(noteId);
            return noteCreated;
        }).when(noteRepository).save(note);
        note = noteService.create(note);
        assertEquals(noteId, note.getId(), "Assert that notes id are equals");
        verify(restTemplate, times(1)).getForObject(anyString(), any(), anyLong());
        verify(noteRepository, times(1)).save(note);
        verify(kfProducer, times(1)).sendMessage(any());
    }

    @Test
    void verifyCreateFailedTest() {
        Note note = NoteFactory.generateNote();
        when(restTemplate.getForObject(anyString(), any(), anyLong())).thenReturn(false);
        assertThrows(ResourceDoesNotExistException.class, ()-> noteService.create(note),
                "Assert ResourceAlreadyExistsException");
        verify(restTemplate, times(1)).getForObject(anyString(), any(), anyLong());
    }

    @Test
    void verifyFindAllByUserIdTest() {
        Note note = NoteFactory.generateNote();
        List<Note> notes = new ArrayList<>(List.of(note));
        Long userId = 1L;
        when(elasticClient.findAllByUserId(anyLong())).thenReturn(notes);
        List<Note> notesFounded = noteService.findAllByUserId(userId);
        assertEquals(notes, notesFounded, "Assert that notes and notesFounded are equals");
        verify(elasticClient, times(1)).findAllByUserId(userId);
    }

    @Test
    void verifyFindAllTest() {
        Note note = NoteFactory.generateNote();
        List<Note> notes = new ArrayList<>(List.of(note));
        when(elasticClient.findAll()).thenReturn(notes);
        List<Note> notesFounded = noteService.findAll();
        assertEquals(notes, notesFounded, "Assert that notes and notesFounded are equals");
        verify(elasticClient, times(1)).findAll();
    }


    @Test
    void verifyFindByIdPassedTest() {
        Note note = NoteFactory.generateNote();
        Long noteId = 1L;
        when(elasticClient.findById(anyLong())).thenReturn(Optional.of(note));
        Note noteFounded = noteService.findById(noteId);
        assertEquals(noteFounded, noteFounded, "Assert that note and noteFounded are equals");
        verify(elasticClient, times(1)).findById(noteId);
    }

    @Test
    void verifyFindByIdFailedTest() {
        Long noteId = 1L;
        when(elasticClient.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceDoesNotExistException.class, () -> noteService.findById(noteId),
                "Assert ResourceDoesNotExistException");
        verify(elasticClient, times(1)).findById(noteId);
    }

    @Test
    void verifyUpdatePassedTest() {
        Note note = NoteFactory.generateNote();
        Long noteId = 1L;
        String newDescription = "new description";
        note.setDescription(newDescription);
        when(noteRepository.findById(anyLong())).thenReturn(Optional.of(note));
        when(noteRepository.save(any())).thenReturn(note);
        Note noteUpdated = noteService.update(note);
        assertEquals(note, noteUpdated, "Assert that notes are equals");
        verify(noteRepository, times(1)).findById(noteId);
        verify(noteRepository, times(1)).save(note);
        verify(kfProducer, times(1)).sendMessage(any());
    }

    @Test
    void verifyUpdateFailedTest() {
        Note note = NoteFactory.generateNote();
        Long noteId = 1L;
        Note updatedNote = NoteFactory.generateNote();
        String themeForMismatch = "new description";
        updatedNote.setTheme(themeForMismatch);
        when(noteRepository.findById(anyLong())).thenReturn(Optional.of(updatedNote));
        assertThrows(IllegalOperationException.class, () -> noteService.update(note),
                "Assert ResourceDoesNotExistException");
        verify(noteRepository, times(1)).findById(noteId);
    }

    @Test
    void verifyDeleteTest() {
        Long noteId = 1L;
        noteService.delete(noteId);
        verify(noteRepository, times(1)).deleteById(noteId);
        verify(kfProducer, times(1)).sendMessage(any());
    }

}
