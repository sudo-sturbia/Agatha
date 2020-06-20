package com.github.sudo_sturbia.agatha.client.model.book;

/**
 * ReadState is one of three possible states a book can be in.
 */
public class ReadState implements BookState
{
    /** Number of book's pages. */
    private final int pages;

    /** State constant. Used in deserialization. */
    private final State TYPE = State.READ;

    /**
     * ReadState's constructor.
     *
     * @param pages number of book's pages.
     * @throws IllegalArgumentException if Book is null.
     */
    ReadState(int pages) throws IllegalArgumentException
    {
        if (pages < 0)
        {
            throw new IllegalArgumentException("Invalid number of pages.");
        }

        this.pages = pages;
    }

    @Override
    public State getState()
    {
        return this.TYPE;
    }

    @Override
    public String  getStateToString()
    {
        return "read";
    }

    @Override
    public int getNumberOfReadPages()
    {
        return this.pages;
    }

    @Override
    public BookState setNumberOfReadPages(int newNumber) throws IllegalArgumentException
    {
        if (newNumber < 0 || newNumber > this.pages)
        {
            throw new IllegalArgumentException("Invalid number of pages.");
        }

        // Transition to "currently-reading"
        if (newNumber < this.pages)
        {
            return new ReadingState(this.pages, newNumber);
        }

        return this;
    }
}
