package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.core.Book;
import com.github.sudo_sturbia.agatha.core.BookBuilder;
import com.github.sudo_sturbia.agatha.core.BookImp;
import com.github.sudo_sturbia.agatha.core.BookState;
import com.github.sudo_sturbia.agatha.core.BookStateDeserializer;
import com.github.sudo_sturbia.agatha.core.Note;
import com.github.sudo_sturbia.agatha.core.NoteDeserializer;
import com.github.sudo_sturbia.agatha.core.ExecutionState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test all server operations on several clients.
 */
public class Integration
{
    /** A map of clients and passwords. */
    private final Map<String, String> clients = new HashMap<>();

    /** A map of clients and books. */
    private final Map<String, List<Book>> books = new HashMap<>();

    /** A map of clients and labels. */
    private final Map<String, List<String>> labels = new HashMap<>();

    /** Name of testing database. */
    private final String dbName = "testDB";

    @BeforeAll
    static void setup()
    {
        TestUtil.setup();
    }

    @DisplayName("Create clients, books, notes, and labels.")
    @Order(1)
    @Test
    void create()
    {
        Gson gson = new Gson();
        Random random = new Random();

        // Create 50 users with random usernames/passwords
        for (int i = 0; i < 50; i++)
        {
            String username = this.randomString();
            String password = this.randomString();

            this.clients.put(username, password);

            String response = Protocol.handle(
                    String.format("CREATE %s:%s", username, password),
                    this.dbName);
            assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                    "CREATE user request failed.");
        }

        // Create 10 books for each client
        for (String username : this.clients.keySet())
        {
            List<Book> bookList = new ArrayList<>();
            for (int i = 0; i < 10; i++)
            {
                Book book = BookBuilder.newBook("Book " + i, random.nextInt(100) + 10)
                                       .state(i % 3 == 0 ? BookState.State.INTERESTED :
                                               (i % 3 == 1 ? BookState.State.CURRENTLY_READING : BookState.State.READ))
                                       .author("author #" + i)
                                       .coverPath("path/to/cover")
                                       .build();

                // Add 5 notes to each book
                for (int j = 0; j < 5; j++)
                {
                    book.addNote("Note #" + j, j);
                }

                bookList.add(book);

                String response = Protocol.handle(
                        String.format("CREATE %s:%s/b/%s", username, this.clients.get(username), gson.toJson(book)),
                        this.dbName);
                assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                        "CREATE book request failed.");
            }

            this.books.put(username, bookList);
        }

        // Create 2 labels for each client
        for (String username : this.clients.keySet())
        {
            List<String> labels = new ArrayList<>();
            for (int i = 0; i < 2; i++)
            {
                String label = "label" + i;
                labels.add(label);

                String response = Protocol.handle(
                        String.format("CREATE %s:%s/l/%s", username, this.clients.get(username), label),
                        this.dbName);
                assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                        "CREATE label request failed.");
            }

            this.labels.put(username, labels);
        }
    }

    @DisplayName("Delete clients, books, notes, and labels.")
    @Order(2)
    @Test
    void delete()
    {
        Gson gson = new Gson();
        Random random = new Random();

        // Select usernames of random users
        List<String> randomUsers = new ArrayList<>();
        for (String username : this.clients.keySet())
        {
            if (random.nextBoolean())
            {
                randomUsers.add(username);
            }
        }

        // Randomly delete something from each selected user
        for (String username : randomUsers)
        {
            int rand = random.nextInt(7);

            String response;
            switch (rand % 7)
            {
                // Delete user
                case 0:
                    String password = this.clients.remove(username);

                    this.books.remove(username);
                    this.labels.remove(username);

                    response = Protocol.handle(
                            String.format("DELETE %s:%s", username, password),
                            this.dbName);
                    assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                            "DELETE user request failed.");
                    break;
                // Delete book
                case 1:
                    Book book = this.books.get(username).remove(0);

                    response = Protocol.handle(
                            String.format("DELETE %s:%s/b/%s", username, this.clients.get(username), book.getName()),
                            this.dbName);
                    assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                            "DELETE book request failed.");
                    break;
                // Delete all books
                case 2:
                    this.books.get(username).clear();

                    response = Protocol.handle(
                            String.format("DELETE %s:%s/b/*", username, this.clients.get(username)),
                            this.dbName);
                    assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                            "DELETE all books request failed.");
                    break;
                // Delete label
                case 3:
                    String label = this.labels.get(username).remove(0);

                    response = Protocol.handle(
                            String.format("DELETE %s:%s/l/%s", username, this.clients.get(username), label),
                            this.dbName);
                    assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                            "DELETE label request failed.");
                    break;
                // Delete all labels
                case 4:
                    this.labels.get(username).clear();

                    response = Protocol.handle(
                            String.format("DELETE %s:%s/l/*", username, this.clients.get(username)),
                            this.dbName);
                    assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                            "DELETE all labels request failed.");
                    break;
                // Delete note
                case 5:
                    String bookName = this.books.get(username).get(0).getName();
                    Note note = this.books.get(username).get(0).getNoteAtPage(0);
                    this.books.get(username).get(0).removeNoteAtPage(0);

                    response = Protocol.handle(
                            String.format("DELETE %s:%s/b/%s/n/%d", username, this.clients.get(username), bookName, note.getPageNumber()),
                            this.dbName);
                    assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                            "DELETE note request failed.");
                    break;
                // Delete all notes
                case 6:
                    String name = this.books.get(username).get(0).getName();
                    this.books.get(username).get(0).clearNotes();

                    response = Protocol.handle(
                            String.format("DELETE %s:%s/b/%s/n/*", username, this.clients.get(username), name),
                            this.dbName);
                    assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                            "DELETE all notes request failed.");
                    break;
            }
        }
    }

    @DisplayName("Update clients, books, notes, and labels.")
    @Order(3)
    @Test
    void update()
    {
        Gson gson = new Gson();
        Random random = new Random();

        // Select usernames of random users
        List<String> randomUsers = new ArrayList<>();
        for (String username : this.clients.keySet())
        {
            if (random.nextBoolean())
            {
                randomUsers.add(username);
            }
        }

        // Randomly update something for each selected user
        for (String username : randomUsers)
        {
            int rand = random.nextInt(2);

            String response;
            switch (rand % 2)
            {
                // Update book's field
                case 0:
                    if (!this.books.get(username).isEmpty())
                    {
                        Book book = this.books.get(username).get(0);
                        book.setAuthor("updated_author");

                        response = Protocol.handle(
                                String.format("UPDATE %s:%s/b/%s/author=updated_author", username, this.clients.get(username), book.getName()),
                                this.dbName);
                        assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                                "UPDATE book's field request failed.");
                    }
                    break;
                // Update note's field
                case 1:
                    if (!this.books.get(username).isEmpty())
                    {
                        Note note = this.books.get(username).get(0).getNoteAtPage(1);
                        note.setNote("Updated note");

                        response = Protocol.handle(
                                String.format("UPDATE %s:%s/b/%s/n/%d/note=Updated note", username, this.clients.get(username), this.books.get(username).get(0), note.getPageNumber()),
                                this.dbName);
                        assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                                "UPDATE note's field request failed.");
                    }

                    break;
            }
        }
    }

    @DisplayName("Read clients, books, notes, and labels.")
    @Order(4)
    @Test
    void read()
    {
        Gson gson = new GsonBuilder().registerTypeAdapter(BookState.class, new BookStateDeserializer())
                                     .registerTypeAdapter(Note.class, new NoteDeserializer())
                                     .create();

        // Verify clients' usernames/passwords
        for (String username : this.clients.keySet())
        {
            String response = Protocol.handle(
                    String.format("READ %s:%s", username, this.clients.get(username)),
                    this.dbName);
            assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                    "READ client failed.");
        }

        // Read names of books
        for (String username : this.clients.keySet())
        {
            String response = Protocol.handle(
                    String.format("READ %s:%s", username, this.clients.get(username)),
                    this.dbName);

            List<String> booksNames = gson.fromJson(response, new TypeToken<List<String>>(){}.getType());

            assertEquals(this.books.get(username).size(), booksNames.size(), "Wrong number of books.");

            for (Book book : this.books.get(username))
            {
                assertTrue(booksNames.contains(book.getName()), "Wrong list of books' names.");
            }
        }

        // Read books and notes
        for (String username : this.clients.keySet())
        {
            for (Book actual : this.books.get(username))
            {
                String response = Protocol.handle(
                        String.format("READ %s:%s/b/%s", username, this.clients.get(username), actual.getName()),
                        this.dbName);

                Book expected = gson.fromJson(response, BookImp.class);
                this.assertBook(actual, expected);
            }
        }
    }

    /**
     * Verify a Book against another.
     *
     * @param expected expected book.
     * @param actual actual book.
     */
    private void assertBook(Book expected, Book actual)
    {
        assertEquals(expected.getName(), actual.getName(), "Wrong name.");
        assertEquals(expected.getNumberOfPages(), actual.getNumberOfPages(), "Wrong number of pages.");
        assertEquals(expected.getNumberOfReadPages(), actual.getNumberOfReadPages(), "Wrong number of read pages.");
        assertEquals(expected.getState(), actual.getState(), "Wrong state.");
        assertEquals(expected.getAuthor(), actual.getAuthor(), "Wrong author.");
        assertEquals(expected.getCoverImagePath(), actual.getCoverImagePath(), "Wrong path to cover image.");

        assertEquals(expected.getNotes().size(), actual.getNotes().size(), "Wrong number of notes.");
        for (int i = 0, size = expected.getNotes().size(); i < size; i++)
        {
            this.assertNote(expected.getNotes().get(i), actual.getNotes().get(i));
        }
    }

    /**
     * Verify a Note against another.
     *
     * @param expected expected note.
     * @param actual actual note.
     */
    private void assertNote(Note expected, Note actual)
    {
        assertEquals(expected.getPageNumber(), actual.getPageNumber(), "Wrong page number.");
        assertEquals(expected.getNote(), actual.getNote(), "Wrong note.");
    }

    /** Generate a random string. */
    private String randomString()
    {
        final int SIZE = 8;
        char[] possibleChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < SIZE; i++)
        {
            builder.append(possibleChars[random.nextInt(possibleChars.length)]);
        }

        return builder.toString();
    }

    @AfterAll
    static void clean()
    {
        TestUtil.clean();
    }
}
