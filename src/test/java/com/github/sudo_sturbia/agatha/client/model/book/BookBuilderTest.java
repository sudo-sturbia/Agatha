package com.github.sudo_sturbia.agatha.client.model.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests creation of Book objects using BookBuilder.
 */
class BookBuilderTest
{
    @DisplayName("Test building correct Book objects.")
    @Test
    void correctBuild()
    {
        Book book = BookBuilder.newBook()
                               .name("Book")
                               .numberOfPages(100)
                               .build();

        assertEquals("Book", book.getName(), "Wrong book name.");
        assertEquals(100, book.getNumberOfPages(), "Wrong number of pages.");
        assertEquals(BookState.State.INTERESTED, book.getState(), "Wrong initial state.");

        book = BookBuilder.newBook()
                          .name("Book")
                          .numberOfPages(150)
                          .state(BookState.State.READ)
                          .author("Author")
                          .coverPath("path/to/cover")
                          .build();

        assertEquals("Book", book.getName(), "Wrong book name.");
        assertEquals(150, book.getNumberOfPages(), "Wrong number of pages.");
        assertEquals(BookState.State.READ, book.getState(), "Wrong initial state.");
        assertEquals("Author", book.getAuthor(), "Wrong author.");
        assertEquals("path/to/cover", book.getCoverImagePath(), "Wrong path to cover.");
    }

    @DisplayName("Test builds that should throw exceptions.")
    @Test
    void throwsException()
    {
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    BookBuilder.newBook()
                               .numberOfPages(100)
                               .build();
                },
                "No name specified."
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    BookBuilder.newBook()
                               .name("Book")
                               .numberOfPages(-10)
                               .build();
                },
                "-ve number of pages."
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    BookBuilder.newBook()
                               .name("Book")
                               .numberOfPages(0)
                               .build();
                },
                "Zero number of pages."
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    BookBuilder.newBook()
                               .numberOfPages(-10)
                               .state(BookState.State.READ)
                               .author("Author")
                               .build();
                },
                "No name and -ve number of pages."
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    BookBuilder.newBook("Book", 100)
                               .numberOfPages(-10)
                               .state(BookState.State.READ)
                               .author("Author")
                               .build();
                },
                "-ve number of pages after initial specification."
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    BookBuilder.newBook("Book", 100)
                               .name(null)
                               .state(BookState.State.READ)
                               .author("Author")
                               .build();
                },
                "No name after initial specification."
        );
    }
}
