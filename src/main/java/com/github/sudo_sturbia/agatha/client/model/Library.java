package com.github.sudo_sturbia.agatha.client.model;

import com.github.sudo_sturbia.agatha.client.model.book.Book;

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
 * <p>
 * Each Library must have at least three labels representing Book's
 * three states. States are the only mutually exclusive labels.
 */
public interface Library
{
    /**
     * Get username of the client who owns the library.
     *
     * @return Username of client attached to library.
     */
    public String getUsername();

    /**
     * Get a list of names of user's books.
     *
     * @return A list containing names of user's books.
     */
    public List<String> getBooksNames();

    /**
     * Get Book with given name.
     *
     * @param name name to search for.
     * @return A Book object with the name, null if requested
     *         name doesn't exist.
     */
    public Book getBookWithName(String name);

    /**
     * Get all books in the library that have the given label.
     *
     * @param label name of the label to search for.
     * @return A list of books that have the label.
     */
    public List<Book> getBooksWithLabel(String label);

    /**
     * Add a book to the collection.
     *
     * @param book book to add.
     */
    public void addBook(Book book);

    /**
     * Add a book with a given label to the collection. If the
     * label already exists, the book is simply added. If not,
     * a new label is created.
     *
     * @param book book to add.
     * @param label name of book's label.
     */
    public void addBook(Book book, String label);

    /**
     * Add a collection of books to the library.
     *
     * @param books books to add.
     */
    public void addBooks(Collection<Book> books);

    /**
     * Add a collection of books with a given label to the library.
     * If the label already exists, books are simply added. If not,
     * a new label is created.
     *
     * @param books books to add to the library.
     * @param label name of books' label.
     */
    public void addBooks(Collection<Book> books, String label);

    /**
     * Add a new label to the library. Label initially has no
     * books, but books can be added later.
     *
     * @param label name of the label to add.
     */
    public void addLabel(String label);

    /**
     * Add a label to a book (adds the book to the collection
     * of books that have the label.)
     *
     * @param bookName name of the book to add the label to.
     * @param label name of the label to add to the book.
     * @throws IllegalArgumentException if given bookName doesn't
     *         exist in the library.
     */
    public void addLabelToBook(String bookName, String label) throws IllegalArgumentException;

    /**
     * Delete the book with the given name from the library.
     *
     * @param name name of the book to delete.
     */
    public void deleteBookWithName(String name);

    /**
     * Delete all books with the given label from the library.
     *
     * @param label label of the book to delete.
     */
    public void deleteBooksWithLabel(String label);

    /**
     * Delete a label. Books marked with the label will be
     * unaffected.
     *
     * @param label name of the label to delete.
     */
    public void deleteLabel(String label);
}
