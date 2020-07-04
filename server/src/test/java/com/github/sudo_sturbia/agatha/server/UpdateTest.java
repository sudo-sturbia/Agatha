package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.core.Book;
import com.github.sudo_sturbia.agatha.core.BookBuilder;
import com.github.sudo_sturbia.agatha.core.Note;
import com.github.sudo_sturbia.agatha.core.NoteImp;
import com.github.sudo_sturbia.agatha.core.ExecutionState;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test handling of UPDATE requests.
 */
public class UpdateTest
{
    @BeforeAll
    static void setup()
    {
        TestUtil.setup();
        TestUtil.create();
    }

    @DisplayName("Test UPDATE book.")
    @Test
    void updateBook()
    {
        final String dbName = "testDB";
        final Gson gson = new Gson();
        final Book book = BookBuilder.newBook("My Book", 50).build();

        String response = Protocol.handle("UPDATE username:password/b/My Book/" + gson.toJson(book), dbName);
        assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                "UPDATE book failed.");
    }

    @DisplayName("Test UPDATE book's field.")
    @Test
    void updateBooksField()
    {
        final String dbName = "testDB";
        String response = Protocol.handle("UPDATE username:password/b/My Book/readPages=50", dbName);
        assertEquals(0, new Gson().fromJson(response, ExecutionState.class).getCode(),
                "UPDATE book's field failed.");
    }

    @DisplayName("Test UPDATE note.")
    @Test
    void updateNote()
    {
        final String dbName = "testDB";
        final Gson gson = new Gson();
        final Note note = new NoteImp(100, "Note #2", 10);

        String response = Protocol.handle("UPDATE username:password/b/My Book/n/10/" + gson.toJson(note), dbName);
        assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                "UPDATE note failed.");
    }

    @DisplayName("Test UPDATE note's field.")
    @Test
    void updateNotesField()
    {
        final String dbName = "testDB";
        String response = Protocol.handle("UPDATE username:password/b/My Book/n/10/note=Note #3", dbName);
        assertEquals(0, new Gson().fromJson(response, ExecutionState.class).getCode(),
                "UPDATE note's field failed.");
    }

    @DisplayName("Test add label to book.")
    @Test
    void addLabel()
    {
        final String dbName = "testDB";
        String response = Protocol.handle("UPDATE username:password/l/label/add/b/My Book", dbName);
        assertEquals(0, new Gson().fromJson(response, ExecutionState.class).getCode(),
                "Add label to book failed.");
    }

    @DisplayName("Test remove label from book.")
    @Test
    void removeLabel()
    {
        final String dbName = "testDB";
        String response = Protocol.handle("UPDATE username:password/l/label/remove/b/My Book", dbName);
        assertEquals(0, new Gson().fromJson(response, ExecutionState.class).getCode(),
                "Remove label from book failed.");
    }

    @AfterAll
    static void clean()
    {
        TestUtil.clean();
    }
}
