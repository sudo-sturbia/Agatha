package com.github.sudo_sturbia.agatha.client.model.book;

import java.util.List;

/**
 * Book represents, well a book. It serves as a specification of
 * methods to be implemented by a book class.
 *
 * A Book must be part of a library, each library is attached
 * to one user, so a Book must be attached to only one user.
 *
 * Each Book should have an ID string. Each ID should be unique
 * to all other books existing in the database.
 *
 * A Book can be in one of three states: read, currently reading,
 * or interested. Book's state can be updated by the user or updated
 * internally by itself.
 *
 * Each book contains a set of notes, each note is attached to a
 * page in the book. Each page, including book cover, can have one
 * note at most. A book with n pages has at most n+1 notes (one for
 * each page + cover). Book cover is considered page number 0.
 */
public interface Book
{
    /**
     * Get book's name.
     *
     * @return A string containing book's name.
     */
    public String getName();

    /**
     * Get book's ID.
     *
     * @return A string containing book's ID.
     */
    public String getID();

    /**
     * Get book's current state.
     *
     * @return an enum representing book's state.
     */
    public BookState.State getState();

    /**
     * Get book's current state formatted as a string.
     *
     * @return a string representing book's state.
     */
    public String getStateToString();

    /**
     * Update book's state.
     *
     * @param state enum representing book's new state.
     */
    public void updateState(BookState.State state);

    /**
     * Get number of pages in the book.
     *
     * @return book's number of pages.
     */
    public int getNumberOfPages();

    /**
     * Get number of read pages in the book.
     * If book's state is read, then number of read pages = number
     * of pages. If book's state is interested, then number of read
     * pages = zero.
     *
     * @return number of read pages in a book.
     */
    public int getNumberOfReadPages();

    /**
     * Update number of read pages in a book. If given value >=
     * number of pages or <= zero, an exception is thrown.
     *
     * @param newNumber new number of read pages.
     * @throws IllegalArgumentException if given value is >= number
     * of pages or < zero.
     */
    public void updateNumberOfReadPages(int newNumber) throws IllegalArgumentException;

    /**
     * Increment number of read pages by given increment. If
     * current number of pages + increment > number of book's
     * pages, an exception is thrown.
     *
     * @param increment number of pages to use for incrementing.
     * @throws IllegalArgumentException if current value + increment
     * are > number of book's pages.
     */
    public void incrementNumberOfReadPages(int increment) throws IllegalArgumentException;

    /**
     * Get path to book's cover image.
     *
     * @return string containing path to cover image.
     */
    public String getCoverImagePath();

    /**
     * Set book's cover image.
     *
     * @param path path to wanted cover image.
     */
    public void setCoverImage(String path);

    /**
     * Get all notes in the book. Returns an empty list if
     * user has created no notes.
     *
     * @return a list of all notes created by the user.
     */
    public List<Note> getNotes();

    /**
     * Get note at a certain (specified) page. An exception is
     * thrown if page's number is >= number of book's pages or
     * <= zero.
     *
     * @param pageNumber number of page to get the note from.
     * @return note at specified page, null if no note exists.
     * @throws IllegalArgumentException if pageNumber is invalid.
     */
    public Note getNoteAtPage(int pageNumber) throws IllegalArgumentException;

    /**
     * Add given note to the book.
     *
     * @param note note to add to the book.
     */
    public void addNote(Note note);

    /**
     * Create a new node containing specified string at specified
     * page number of the book. Throws exception if given page
     * contains a note already or given page number is >= number
     * of book's pages or <= zero.
     *
     * @param noteText string containing note's text.
     * @param pageNumber number of page to add note to.
     * @throws IllegalArgumentException if pageNumber is invalid
     *         or page contains a note.
     */
    public void addNote(String noteText, int pageNumber) throws IllegalArgumentException;

    /**
     * Remove note at specified page.
     *
     * @param pageNumber page to remove note from.
     */
    public void removeNoteAtPage(int pageNumber);

    /**
     * Clear all notes created in the book.
     */
    public void clearNotes();

    /**
     * Write all created notes to a text file at a given path.
     *
     * @param path path of a file to write notes to.
     */
    public void writeNotesToPath(String path);
}
