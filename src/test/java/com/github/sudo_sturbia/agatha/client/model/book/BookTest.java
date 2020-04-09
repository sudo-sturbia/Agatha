package com.github.sudo_sturbia.agatha.client.model.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * BookTest tests Book, BookImp, and BookBuilder.
 */
class BookTest
{
    @DisplayName("Test BookBuilder.build methods.")
    @Test
    void create()
    {
        // Correct creation
        assertNotNull(BookBuilder.build("Book", 10));
        assertNotNull(BookBuilder.build("Book", 10, "path"));
        assertNotNull(BookBuilder.build("Book", BookState.State.READ, 10));
        assertNotNull(BookBuilder.build("Book", BookState.State.READ, 10, "path"));

        // Should throw exception
        assertThrows(IllegalArgumentException.class, () -> BookBuilder.build(null, 10),
                "Book without a name.");

        assertThrows(IllegalArgumentException.class, () -> BookBuilder.build("Book", -1),
                "Book with -ve number of pages.");

        assertThrows(IllegalArgumentException.class, () -> BookBuilder.build("Book", -100, "path"),
                "Book with -ve number of pages.");

        assertThrows(IllegalArgumentException.class, () -> BookBuilder.build("Book", null, 10),
                "Book with null State");

        assertThrows(IllegalArgumentException.class, () -> BookBuilder.build(null, BookState.State.READ, 10),
                "Book without a name.");
    }

    @DisplayName("Test Book's getters.")
    @Test
    void get()
    {
        Book book_1 = BookBuilder.build("Book", 100);

        assertEquals("Book", book_1.getName(), "Wrong name.");
        assertEquals(100, book_1.getNumberOfPages(), "Wrong number of pages.");
        assertEquals(0, book_1.getNumberOfReadPages(), "Wrong number of read pages.");
        assertEquals(BookState.State.INTERESTED, book_1.getState(), "Wrong default state.");
        assertEquals("interested", book_1.getStateToString(), "Wrong state string.");
        assertNull(book_1.getCoverImagePath(), "No cover path.");
        assertEquals(0, book_1.getNotes().size(), "No notes created.");

        Book book_2 = BookBuilder.build("Book", BookState.State.READ, 150, "coverPath");

        assertEquals("Book", book_2.getName(), "Wrong name.");
        assertEquals(150, book_2.getNumberOfPages(), "Wrong number of pages.");
        assertEquals(150, book_2.getNumberOfReadPages(), "Wrong number of read pages.");
        assertEquals(BookState.State.READ, book_2.getState(), "Wrong default state.");
        assertEquals("read", book_2.getStateToString(), "Wrong state string.");
        assertEquals("coverPath", book_2.getCoverImagePath(), "Wrong cover path.");
        assertEquals(0, book_2.getNotes().size(), "Wrong number of notes.");
    }

    @DisplayName("Test states, and state transition.")
    @Test
    void state()
    {
        // In default state (InterestedState).
        Book book = BookBuilder.build("Test Book", 100);

        assertEquals(BookState.State.INTERESTED, book.getState(), "Wrong default state.");
        assertEquals("interested", book.getStateToString(), "Wrong state string.");

        assertEquals(0, book.getNumberOfReadPages(),
                "Wrong number of read pages for interested state.");

        // Update state to read
        book.updateState(BookState.State.READ);
        assertEquals(100, book.getNumberOfReadPages(),
                "Wrong number of read pages for read state.");

        // Update state to interested
        book.updateState(BookState.State.INTERESTED);
        assertEquals(0, book.getNumberOfReadPages(),
                "Wrong number of read pages for interested state.");

        // Update number of read pages
        // New state should be currently reading
        book.updateNumberOfReadPages(15);
        assertEquals(15, book.getNumberOfReadPages(),
                "Wrong number of read pages.");

        assertEquals(BookState.State.CURRENTLY_READING, book.getState(), "Wrong state after update.");
        assertEquals("reading", book.getStateToString(), "Wrong state string.");

        // Update number of read pages
        // State should remain as currently reading
        book.updateNumberOfReadPages(30);
        assertEquals(30, book.getNumberOfReadPages(),
                "Wrong number of read pages.");

        assertEquals(BookState.State.CURRENTLY_READING, book.getState(), "Wrong state after update.");
        assertEquals("reading", book.getStateToString(), "Wrong state string.");

        // Update number of read pages
        // New state should be read
        book.updateNumberOfReadPages(100);
        assertEquals(100, book.getNumberOfReadPages(),
                "Wrong number of read pages.");

        assertEquals(BookState.State.READ, book.getState(), "Wrong state after update.");
        assertEquals("read", book.getStateToString(), "Wrong state string.");

        // Update state to currently reading
        book.updateState(BookState.State.CURRENTLY_READING);
        assertEquals(0, book.getNumberOfReadPages(),
                "Wrong number of read pages for reading state.");
    }

    @DisplayName("Test updating read pages.")
    @Test
    void readPages()
    {
        Book book = BookBuilder.build("Test Book.", 100);

        assertEquals(0, book.getNumberOfReadPages(),
                "Wrong number of read pages for interested state.");

        book.updateNumberOfReadPages(10);
        book.incrementNumberOfReadPages(13);

        assertEquals(23, book.getNumberOfReadPages(), "Wrong number after increment.");

        // Should throw exception
        assertThrows(IllegalArgumentException.class, () -> book.updateNumberOfReadPages(-1),
                "-ve number of read pages.");

        assertThrows(IllegalArgumentException.class, () -> book.updateNumberOfReadPages(101),
                "Number of read pages > number of book's pages.");

        assertThrows(IllegalArgumentException.class, () -> book.incrementNumberOfReadPages(-32),
                "-ve number of read pages.");

        assertThrows(IllegalArgumentException.class, () -> book.incrementNumberOfReadPages(130),
                "Number of read pages > number of book's pages.");
    }

    @DisplayName("Test note operations.")
    @Test
    void note()
    {
        Book book = BookBuilder.build("Book", 100);

        assertEquals(0, book.getNotes().size(), "No notes created.");
        assertNull(book.getNoteAtPage(1), "No note exists.");

        book.addNote(new NoteImp(book, "Note", 10));
        assertEquals(1, book.getNotes().size(), "Created one note.");

        assertThrows(IllegalArgumentException.class, () -> book.addNote(null, 15),
                "Note contains no text.");
        assertThrows(IllegalArgumentException.class, () -> book.addNote("Note", 10),
                "Another note exists at given position.");
        assertThrows(IllegalArgumentException.class, () -> book.addNote("Note", -21),
                "Invalid page number.");

        book.removeNoteAtPage(10);
        assertEquals(0, book.getNotes().size(), "No notes exist.");
    }

    @DisplayName("Test note operations on a collection of notes.")
    @Test
    void noteCollection()
    {
        Book book = BookBuilder.build("Book", 10000);

        assertEquals(0, book.getNotes().size(), "No notes created.");

        // Add a note to every page
        for (int i = 0; i <= 10000; i++)
        {
            book.addNote("Note " + i, i);
        }

        assertEquals(10001, book.getNotes().size(), "Every page should contain a note.");

        // Add to any page
        Random random = new Random();
        for (int i = 0; i < 100; i++)
        {
            assertThrows(IllegalArgumentException.class,
                    () -> book.addNote(new NoteImp(book, "replacement", random.nextInt(10001))),
                    "A note already exists in page.");
        }

        for (int i = 0; i <= 10000; i++)
        {
            Note note = book.getNoteAtPage(i);

            assertNotNull(note, "A note exists at page.");
            assertEquals("Note " + i, note.getNote(), "Wrong text note.");
        }
    }
}
