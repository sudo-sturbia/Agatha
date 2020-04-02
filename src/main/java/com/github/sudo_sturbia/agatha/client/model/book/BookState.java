package com.github.sudo_sturbia.agatha.client.model.book;

/**
 * BookState represents the current state of a book object.
 * BookState can be one of three options: read, currently
 * reading, or interested, each of which is represented by
 * an enum and a class.
 */
public interface BookState
{
    /** Constants representing the three possible book states. */
    public enum State
    {
        READ, CURRENTLY_READING, INTERESTED
    }

    /**
     * Get BookState's type.
     *
     * @return an enum representing BookState's type.
     */
    public State getState();

    /**
     * Get BookState's type formatted as a string.
     *
     * @return a string representing BookState's type.
     */
    public String getStateToString();

    /**
     * Get number of read pages in a book.
     *
     * @return number of read pages in a book.
     */
    public int getNumberOfReadPages();

    /**
     * Update number of read pages in a book and return a
     * BookState object. Book's state my remain the same or
     * change based on given input.
     *
     * @param newNumber new number of read pages.
     * @return a BookState object representing book's current
     *         state.
     * @throws IllegalArgumentException if given number is >
     *         number of book's pages or < zero.
     */
    public BookState updateNumberOfReadPages(int newNumber) throws IllegalArgumentException;
}
