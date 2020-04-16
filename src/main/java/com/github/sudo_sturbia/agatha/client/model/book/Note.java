package com.github.sudo_sturbia.agatha.client.model.book;

/**
 * Note represents a text node that is attached to a certain
 * page in a book. A note contains references to both the book
 * and the page. A book can have at most one note per page.
 */
public interface Note
{
    /**
     * Get text note.
     *
     * @return A string containing text note.
     */
    public String getNote();

    /**
     * Update text note (string).
     *
     * @param newNote string to replace current text.
     * @throws IllegalArgumentException if newNote is null.
     */
    public void setNote(String newNote) throws IllegalArgumentException;

    /**
     * Get note's page number.
     *
     * @return Page number to which note is attached.
     */
    public int getPageNumber();

    /**
     * Update the page to which the note is attached. The
     * method updates both the note object and the book to
     * which the note is attached.
     *
     * @param newPage number of new page.
     * @throws IllegalArgumentException if the book already has
     *         a note in given page, or if newPage &gt; number of
     *         book's pages or &lt; 0.
     */
    public void setPageNumber(int newPage) throws IllegalArgumentException;
}
