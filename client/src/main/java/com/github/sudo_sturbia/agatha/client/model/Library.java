package com.github.sudo_sturbia.agatha.client.model;

import com.github.sudo_sturbia.agatha.core.Book;
import com.github.sudo_sturbia.agatha.core.BookImp;
import com.github.sudo_sturbia.agatha.core.ExecutionState;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Library represents an application client. It's a container
 * for a collection of <code>Book</code>s.
 * <p>
 * A Library represents a client's entry in the application's
 * database. It is the middle communication point between the
 * application's GUI and the server. It provides a set of operations
 * that deal with individual books, collections of books, or
 * the entire database.
 * <p>
 * As Library represents a client, each Library should store
 * both a username and a password. Each username should be unique
 * to all other usernames in the database. Each <code>Book</code>
 * in the Library should have a unique name to be used as a key.
 * <p>
 * Library arranges books according to labels. A label is simple
 * string created by the user to describe a group of books. A Book
 * can have any number of labels.
 */
public class Library
{
    /** Library's Communicator. */
    private final Communicator communicator;

    /** Private constructor. */
    private Library(Communicator communicator)
    {
        this.communicator = communicator;
    }

    /**
     * Get user's library. Verifies given username and password, and
     * returns a library representing the client if credentials are
     * correct.
     *
     * @param username client's username.
     * @param password client's password.
     * @return Client's library if credentials are correct, null otherwise.
     */
    public static Library getLibrary(String username, String password, int port, String host)
    {
        Communicator communicator = new Communicator(username, password, port, host);

        // Verify that server is running
        if (communicator.isServerRunning())
        {
            ExecutionState state = communicator.request(ExecutionState.class, Communicator.FUNCTION.READ, "");
            if (state.getCode() == 0)
            {
                return new Library(communicator);
            }
        }

        return null;
    }

    /**
     * Get username of the client who owns the library.
     *
     * @return Username of client attached to library.
     */
    public String getUsername()
    {
        return this.communicator.getUsername();
    }

    /**
     * Get a list of names of user's books.
     *
     * @return A list containing names of user's books.
     */
    public List<String> getNamesOfBooks()
    {
        return new ArrayList<>(Arrays.asList(
                this.communicator.request(String[].class, Communicator.FUNCTION.READ, "/b/*")));
    }

    /**
     * Get a list of name of books with given label.
     *
     * @param label label to search for.
     * @return A list containing name of books that have the label.
     */
    public List<String> getNamesOfBooksWithLabel(String label)
    {
        return new ArrayList<>(Arrays.asList(
                this.communicator.request(String[].class, Communicator.FUNCTION.READ, String.format("/l/%s", label))));
    }

    /**
     * Get Book with given name.
     *
     * @param name name to search for.
     * @return A Book object with the name, null if requested
     *         name doesn't exist.
     */
    public Book getBookWithName(String name)
    {
        return this.communicator.request(BookImp.class, Communicator.FUNCTION.READ, String.format("/b/%s", name));
    }

    /**
     * Update a book that already exists in the library.
     *
     * @param book book to update.
     * @return Execution state.
     */
    public ExecutionState updateBook(Book book)
    {
        return this.communicator.request(ExecutionState.class, Communicator.FUNCTION.UPDATE, String.format("/b/%s/%s", book.getName(), new Gson().toJson(book)));
    }

    /**
     * Add a book to the collection.
     *
     * @param book book to add.
     * @return Execution state.
     */
    public ExecutionState addBook(Book book)
    {
        return this.communicator.request(ExecutionState.class, Communicator.FUNCTION.CREATE, String.format("/b/%s", new Gson().toJson(book)));
    }

    /**
     * Add a book with a given label to the collection. If the
     * label already exists, the book is simply added. If not,
     * a new label is created.
     *
     * @param book book to add.
     * @param label name of book's label.
     * @return Execution state.
     */
    public ExecutionState addBook(Book book, String label)
    {
        this.addLabel(label);

        ExecutionState state = this.addBook(book);
        if (state == null || state.getCode() != 0) // Something wrong with the book
        {
            return state;
        }

        return this.communicator.request(ExecutionState.class, Communicator.FUNCTION.UPDATE, String.format("/l/%s/add/b/%s", label, book.getName()));
    }

    /**
     * Add a collection of books to the library.
     *
     * @param books books to add.
     */
    public void addBooks(Collection<Book> books)
    {
        for (Book book : books)
        {
            this.addBook(book);
        }
    }

    /**
     * Add a collection of books with a given label to the library.
     * If the label already exists, books are simply added. If not,
     * a new label is created.
     *
     * @param books books to add to the library.
     * @param label name of books' label.
     */
    public void addBooks(Collection<Book> books, String label)
    {
        this.addLabel(label);

        for (Book book : books)
        {
            this.addBook(book);
            this.communicator.request(ExecutionState.class, Communicator.FUNCTION.UPDATE, String.format("/l/%s/add/b/%s", label, book.getName()));
        }
    }

    /**
     * Add a new label to the library. Label initially has no
     * books, but books can be added later.
     *
     * @param label name of the label to add.
     * @return Execution state.
     */
    public ExecutionState addLabel(String label)
    {
        return this.communicator.request(ExecutionState.class, Communicator.FUNCTION.CREATE, String.format("/l/%s", label));
    }

    /**
     * Add a label to a book (adds the book to the collection
     * of books that have the label.)
     *
     * @param bookName name of the book to add the label to.
     * @param label name of the label to add to the book.
     * @return Execution state.
     * @throws IllegalArgumentException if given bookName doesn't
     *         exist in the library.
     */
    public ExecutionState addLabelToBook(String bookName, String label) throws IllegalArgumentException
    {
        return this.communicator.request(ExecutionState.class, Communicator.FUNCTION.UPDATE, String.format("/l/%s/add/b/%s", label, bookName));
    }

    /**
     * Delete the book with the given name from the library.
     *
     * @param name name of the book to delete.
     * @return Execution state.
     */
    public ExecutionState deleteBookWithName(String name)
    {
        return this.communicator.request(ExecutionState.class, Communicator.FUNCTION.DELETE, String.format("/b/%s", name));
    }

    /**
     * Delete all books with the given label from the library.
     *
     * @param label label of the book to delete.
     */
    public void deleteBooksWithLabel(String label)
    {
        List<String> books = this.getNamesOfBooksWithLabel(label);
        for (String book : books)
        {
            this.deleteBookWithName(book);
        }
    }

    /**
     * Delete a label. Books marked with the label will be
     * unaffected.
     *
     * @param label name of the label to delete.
     * @return Execution state.
     */
    public ExecutionState deleteLabel(String label)
    {
        return this.communicator.request(ExecutionState.class, Communicator.FUNCTION.DELETE, String.format("/l/%s", label));
    }

    /**
     * Delete label from book.
     *
     * @param bookName name of book to delete label from.
     * @param label name of label to delete.
     * @return Execution state.
     */
    public ExecutionState deleteLabelFromBook(String bookName, String label)
    {
        return this.communicator.request(ExecutionState.class, Communicator.FUNCTION.UPDATE, String.format("/l/%s/remove/b/%s", label, bookName));
    }
}
