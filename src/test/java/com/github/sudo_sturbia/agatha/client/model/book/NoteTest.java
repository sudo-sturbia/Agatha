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
        note.setNote("Updated note");
        assertEquals("Updated note", note.getNote(), "Wrong text after update.");

        // Should throw exception
        assertThrows(IllegalArgumentException.class, () -> note.setNote(null),
                "No message given.");
    }
}
