package com.github.sudo_sturbia.agatha.client.model.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test Note's implementation.
 */
class NoteTest
{
    @DisplayName("Test note creation.")
    @Test
    void create()
    {
        Book book = BookBuilder.newBook()
                               .name("Book")
                               .numberOfPages(100)
                               .build();

        // Should throw exception
        assertThrows(IllegalArgumentException.class, () -> new NoteImp(null, "Note", 10),
                "Not Book reference given.");

        assertThrows(IllegalArgumentException.class, () -> new NoteImp(book, null, 10),
                "No text-note given.");

        assertThrows(IllegalArgumentException.class, () -> new NoteImp(book, "Note", -1),
                "Page number is -ve.");

        assertThrows(IllegalArgumentException.class, () -> new NoteImp(book, "Note", 101),
                "Page number > book's number of pages.");

        // Create a note at page 10
        book.addNote(new NoteImp(book, "Note", 10));
        assertThrows(IllegalArgumentException.class, () -> new NoteImp(book, "Note", 10),
                "Two notes at the same page.");
    }

    @DisplayName("Test update page number.")
    @Test
    void updatePageNumber()
    {
        Book book = BookBuilder.newBook()
                               .name("Book")
                               .numberOfPages(100)
                               .build();

        Note note = new NoteImp(book, "Note", 10);

        // Correct update
        note.updatePageNumber(30);
        assertEquals(30, note.getPageNumber(), "Wrong page number after update.");

        // Should throw exception
        assertThrows(IllegalArgumentException.class, () -> note.updatePageNumber(-1),
                "Updated page number is -ve.");

        assertThrows(IllegalArgumentException.class, () -> note.updatePageNumber(101),
                "Updated page number > Book's number of pages.");

        book.addNote(new NoteImp(book, "Note", 15));
        assertThrows(IllegalArgumentException.class, () -> note.updatePageNumber(15),
                "Two notes at the same page.");
    }

    @DisplayName("Test update note.")
    @Test
    void updateNote()
    {
        Book book = BookBuilder.newBook()
                               .name("Book")
                               .numberOfPages(100)
                               .build();

        Note note = new NoteImp(book, "Note", 10);

        // Correct update
        note.updateNote("Updated note");
        assertEquals("Updated note", note.getNote(), "Wrong text after update.");

        // Should throw exception
        assertThrows(IllegalArgumentException.class, () -> note.updateNote(null),
                "No message given.");
    }
}
