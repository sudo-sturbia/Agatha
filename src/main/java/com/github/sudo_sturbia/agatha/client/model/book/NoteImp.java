package com.github.sudo_sturbia.agatha.client.model.book;

/**
 * NoteImp implements Note interface and represents a text note
 * in a book. NoteImp contains the page number to which the note
 * is attached. Page count starts at 0, with 0 being book's cover.
 * A book with n pages can have at most n+1 notes. Page indices
 * start at 0 and end at n.
 */
public class NoteImp implements Note
{
    /** Text note. */
    private String note;

    /** Page to which the note is attached. */
    private final int pageNumber;

    /**
     * NodeImp's constructor.
     *
     * @param book the book containing the note.
     * @param note text note.
     * @param pageNumber the number of the page to which the
     *                   note is attached.
     * @throws IllegalArgumentException if book or note are null,
     *         or page number is &gt; number of book's pages or &lt; 0
     *         or if page already contains a note.
     */
    public NoteImp(Book book, String note, int pageNumber) throws IllegalArgumentException
    {
        if (book == null)
        {
            throw new IllegalArgumentException("No book is given.");
        }
        else if (note == null)
        {
            throw new IllegalArgumentException("No note is given.");
        }
        else if (pageNumber < 0 || pageNumber > book.getNumberOfPages())
        {
            throw new IllegalArgumentException("Invalid number of read pages.");
        }
        else if (book.getNoteAtPage(pageNumber) != null)
        {
            throw new IllegalArgumentException("Page " + pageNumber +
                    " already contains a note.");
        }

        this.note = note;
        this.pageNumber = pageNumber;
    }

    @Override
    public String getNote()
    {
        return this.note;
    }

    @Override
    public void setNote(String newNote) throws IllegalArgumentException
    {
        if (newNote == null)
        {
            throw new IllegalArgumentException("No note is given.");
        }

        this.note = newNote;
    }

    @Override
    public int getPageNumber() {
        return this.pageNumber;
    }
}
