package com.github.sudo_sturbia.agatha.client.model.book;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * BookImp is an implementation of the Book interface.
 */
public class BookImp implements Book {
    /** Book's name. */
    private String name;

    /** Book's current state. */
    private BookState state;

    /** Number of book's pages. */
    private int pages;

    /** Path to book's cover image. */
    private String coverPath;

    /** Map containing book's notes. Maps pages' numbers to notes. */
    private Map<Integer, Note> notes;

    /**
     * BookImpl's constructor. The constructor creates a BookImp
     * object with state "interested". To update state use
     * updateState method.
     *
     * The constructor is package-private as it is not meant to
     * be used outside of the package, BookBuilder should be
     * used instead.
     *
     * @param name book's name.
     * @param pages number of book's pages.
     * @param coverPath path to cover image (optional, can e null).
     * @throws IllegalArgumentException if name, or State are null,
     *         or pages <= zero.
     */
    BookImp(String name, int pages, String coverPath) throws IllegalArgumentException
    {
        if (name == null)
        {
            throw new IllegalArgumentException("No name is given.");
        }
        else if (pages <= 0)
        {
            throw new IllegalArgumentException("Invalid number of pages.");
        }

        this.name = name;
        this.pages = pages;
        this.coverPath = coverPath;

        this.state = new InterestedState(this);

        this.notes = new HashMap<>();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public BookState.State getState()
    {
        return this.state.getState();
    }

    @Override
    public String getStateToString()
    {
        return this.state.getStateToString();
    }

    @Override
    public void updateState(BookState.State state) throws IllegalArgumentException
    {
        if (state == null)
        {
            throw new IllegalArgumentException("No state given.");
        }

        switch (state)
        {
            case READ:
                this.state = new ReadState(this);
                break;
            case CURRENTLY_READING:
                this.state = new ReadingState(this, 0);
                break;
            case INTERESTED:
                this.state = new InterestedState(this);
                break;
        }
    }

    @Override
    public int getNumberOfPages()
    {
        return this.pages;
    }

    @Override
    public int getNumberOfReadPages()
    {
        return this.state.getNumberOfReadPages();
    }

    @Override
    public void updateNumberOfReadPages(int newNumber) throws IllegalArgumentException
    {
        this.state = this.state.updateNumberOfReadPages(newNumber);
    }

    @Override
    public void incrementNumberOfReadPages(int increment) throws IllegalArgumentException
    {
        this.updateNumberOfReadPages(this.state.getNumberOfReadPages() + increment);
    }

    @Override
    public String getCoverImagePath()
    {
        return this.coverPath;
    }

    @Override
    public void setCoverImage(String path)
    {
        this.coverPath = path;
    }

    @Override
    public List<Note> getNotes()
    {
        return new LinkedList<>(this.notes.values());
    }

    @Override
    public Note getNoteAtPage(int pageNumber) throws IllegalArgumentException
    {
        if (pageNumber < 0 || pageNumber > this.pages)
        {
            throw new IllegalArgumentException("Invalid number of pages.");
        }

        return this.notes.get(pageNumber);
    }

    @Override
    public void addNote(Note note)
    {
        this.notes.put(note.getPageNumber(), note);
    }

    @Override
    public void addNote(String noteText, int pageNumber) throws IllegalArgumentException
    {
        // Create a new note
        Note note = new NoteImp(this, noteText, pageNumber);

        this.notes.put(pageNumber, note);
    }

    @Override
    public void removeNoteAtPage(int pageNumber)
    {
        this.notes.remove(pageNumber);
    }

    @Override
    public void clearNotes()
    {
        this.notes.clear();
    }

    @Override
    public void writeNotesToPath(String path)
    {
        // TODO ...
    }
}
