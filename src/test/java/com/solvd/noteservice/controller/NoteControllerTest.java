package com.solvd.noteservice.controller;

import com.solvd.noteservice.domain.Note;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;

@SpringBootTest
@AutoConfigureGraphQlTester
public class NoteControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Test
    void shouldCreateTest() {
        Note noteCreated = this.graphQlTester.documentName("create-mutation")
                .execute()
                .path("data.create")
                .entity(Note.class)
                .get();
        Assertions.assertNotNull(noteCreated);
    }

    @Test
    void shouldFindByIdTest() {
        Note noteCreated = this.graphQlTester.documentName("create-mutation")
                .execute()
                .path("data.create")
                .entity(Note.class)
                .get();
        Note noteFounded = this.graphQlTester.documentName("findById-query")
                .variable("id", noteCreated.getId())
                .execute()
                .path("data.findById")
                .entity(Note.class)
                .get();
        Assertions.assertNotNull(noteFounded);
        Assertions.assertEquals(noteCreated.getId(), noteFounded.getId());
        Assertions.assertEquals(noteCreated.getTheme(), noteFounded.getTheme());
    }

    @Test
    void shouldFindAllTest() {
        this.graphQlTester.documentName("create-mutation")
                .execute()
                .path("data.create")
                .entity(Note.class);
        List<Note> notesFounded = this.graphQlTester.documentName("findAll-query")
                .execute()
                .path("data.findAll")
                .entityList(Note.class)
                .get();
        Assertions.assertNotNull(notesFounded);
        Assertions.assertNotEquals(notesFounded.size(), 0);
    }

    @Test
    void shouldCheckForExistingTest() {
        Note noteCreated = this.graphQlTester.documentName("create-mutation")
                .execute()
                .path("data.create")
                .entity(Note.class)
                .get();
        Boolean result = this.graphQlTester.documentName("isExistById-query")
                .variable("id", noteCreated.getId())
                .execute()
                .path("data.isExistById")
                .entity(Boolean.class)
                .get();
        Assertions.assertTrue(result);
    }

    @Test
    void shouldFindAllByUserIdTest() {
        Note noteCreated = this.graphQlTester.documentName("create-mutation")
                .execute()
                .path("data.create")
                .entity(Note.class)
                .get();
        List<Note> notesFounded = this.graphQlTester.documentName("findAllByUserId-query")
                .variable("userId", noteCreated.getUserId())
                .execute()
                .path("data.findAllByUserId")
                .entityList(Note.class)
                .get();
        Assertions.assertNotNull(notesFounded);
        Assertions.assertNotEquals(notesFounded.size(), 0);
    }

    @Test
    void shouldUpdateTest() {
        Note noteCreated = this.graphQlTester.documentName("create-mutation")
                .execute()
                .path("data.create")
                .entity(Note.class)
                .get();
        Note noteUpdated = this.graphQlTester.documentName("update-mutation")
                .variable("id", noteCreated.getId())
                .execute()
                .path("data.update")
                .entity(Note.class)
                .get();
        Assertions.assertNotNull(noteUpdated);
    }

    @Test
    void shouldDeleteTest() {
        Note noteCreated = this.graphQlTester.documentName("create-mutation")
                .execute()
                .path("data.create")
                .entity(Note.class)
                .get();
        this.graphQlTester.documentName("delete-mutation")
                .variable("id", noteCreated.getId())
                .execute()
                .path("data.delete");
        Boolean result = this.graphQlTester.documentName("isExistById-query")
                .variable("id", noteCreated.getId())
                .execute()
                .path("data.isExistById")
                .entity(Boolean.class)
                .get();
        Assertions.assertFalse(result);
    }

}
