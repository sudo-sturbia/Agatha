package com.github.sudo_sturbia.agatha.client.model;

import com.github.sudo_sturbia.agatha.client.model.book.Book;

import java.util.Collection;
import java.util.List;

/**
 * Library represents an application client. It's a container
 * for a collection of books.
 * <p>
 * A Library manages the client's entry in application's database
 * and provides a collection of CRUD operations that deal with
 * individual books, different collections of books, or the entire
 * library. It's also meant to be the point of interaction between
 * the application's gui and the server.
 * <p>
 * The main jop of a Library is to manage communication with
 * the application Server.
 * <p>
 * Each client must have a username unique to all others in the
 * application's database and a password. Each Book contained in
 * a client's Library must have an ID unique to all other books in
 * the Library.
 * <p>
 * Each Library arranges books according to labels. A label is a
 * simple string created by the user. A Book can have any number of
 * labels.
 * <p>
 * Each Library must have at least three labels representing Book's
 * three states. States are the only mutually exclusive labels.
 */
public interface Library
{
    /**
     * Get user name of the client who owns the library.
     *
     * @return Username of client attached to library.
     */
    public String getUsername();

    /**
     * Get all books in the library that have the given name.
     *
     * @param name name to search for.
     * @return A list of books that have the name.
     */
    public List<Book> getBooksWithName(String name);

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
     * Delete all books with the given name from the library.
     *
     * @param name name of the book to delete.
     */
    public void deleteBooksWithName(String name);

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
