package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.client.model.book.Book;
import com.github.sudo_sturbia.agatha.client.model.book.BookBuilder;
import com.github.sudo_sturbia.agatha.client.model.book.BookImp;
import com.github.sudo_sturbia.agatha.client.model.book.BookState;
import com.github.sudo_sturbia.agatha.client.model.book.BookStateDeserializer;
import com.github.sudo_sturbia.agatha.client.model.book.Note;
import com.github.sudo_sturbia.agatha.client.model.book.NoteDeserializer;
import com.github.sudo_sturbia.agatha.client.model.book.NoteImp;
import com.github.sudo_sturbia.agatha.server.request.ExecutionState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test handling of READ requests.
 */
public class ReadTest
{
    @BeforeAll
    static void setup()
    {
        TestUtil.setup();
        TestUtil.create();
    }

    @DisplayName("Test READ user.")
    @Test
    void readUser()
    {
        final String dbName = "testDB";
        String response = Protocol.handle("READ username:password", dbName);
        assertEquals(0, new Gson().fromJson(response, ExecutionState.class).getCode(),
                "Failed to verify user.");
    }

    @DisplayName("Test READ book.")
    @Test
    void readBook()
    {
        final String dbName = "testDB";
        final Gson gson = new GsonBuilder().registerTypeAdapter(BookState.class, new BookStateDeserializer())
                                           .registerTypeAdapter(Note.class, new NoteDeserializer())
                                           .create();

        final Book book = BookBuilder.newBook("My Book", 100).build();
        final Note note = new NoteImp(100, "Note #1", 10);
        book.addNote(note);

        Book read = gson.fromJson(Protocol.handle("READ username:password/b/My Book", dbName), BookImp.class);

        // Assertions.
        assertEquals(book.getName(), read.getName(), "Wrong book name.");
        assertEquals(book.getNumberOfPages(), read.getNumberOfPages(), "Wrong number of pages.");
        assertEquals(book.getNumberOfReadPages(), read.getNumberOfReadPages(), "Wrong number of read pages.");
        assertEquals(book.getState(), read.getState(), "Wrong state.");
        assertEquals(book.getAuthor(), read.getAuthor(), "Wrong author.");
        assertEquals(book.getCoverImagePath(), read.getCoverImagePath(), "Wrong cover path.");

        assertEquals(book.getNotes().size(), read.getNotes().size(), "Wrong number of notes.");
        assertEquals(book.getNotes().get(0).getNote(), read.getNotes().get(0).getNote(),
                "Wrong note at page #10.");
        assertEquals(book.getNotes().get(0).getPageNumber(), read.getNotes().get(0).getPageNumber(),
                "Wrong page number for note.");
    }

    @DisplayName("Test READ books' names.")
    @Test
    void readBooksNames()
    {
        final String dbName = "testDB";
        final Gson gson = new Gson();

        List<String> names = gson.fromJson(Protocol.handle("READ username:password/b/*", dbName),
                new TypeToken<List<String>>(){}.getType());

        assertEquals(1, names.size(), "Wrong number of books.");
        assertEquals("My Book", names.get(0), "Wrong book name.");
    }

    @DisplayName("Test READ books' names.")
    @Test
    void readBooksWithLabel()
    {
        final String dbName = "testDB";
        final Gson gson = new Gson();

        List<String> names = gson.fromJson(Protocol.handle("READ username:password/l/label", dbName),
                new TypeToken<List<String>>(){}.getType());

        assertEquals(0, names.size(), "Wrong number of books.");
    }

    @AfterAll
    static void clean()
    {
        TestUtil.clean();
    }
}
