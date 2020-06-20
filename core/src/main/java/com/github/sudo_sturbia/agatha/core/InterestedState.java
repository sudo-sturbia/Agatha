package com.github.sudo_sturbia.agatha.core;

/**
 * InterestedState is one of three possible states a book can be in.
 */
public class InterestedState implements BookState
{
    /** Number of book's pages. */
    private final int pages;

    /** State constant. Used in deserialization. */
    private final State TYPE = State.INTERESTED;

    /**
     * InterestedState's constructor.
     *
     * @param pages number of book's pages.
     * @throws IllegalArgumentException if book is null.
     */
    InterestedState(int pages) throws IllegalArgumentException
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
    public String getStateToString()
    {
        return "interested";
    }

    @Override
    public int getNumberOfReadPages()
    {
        return 0;
    }

    @Override
    public BookState setNumberOfReadPages(int newNumber) throws IllegalArgumentException
    {
        if (newNumber < 0 || newNumber > this.pages)
        {
            throw new IllegalArgumentException("Invalid number of read pages.");
        }

        // Transition to "read"
        if (newNumber == this.pages)
        {
            return new ReadState(this.pages);
        }
        // Transition to "currently reading"
        else if (newNumber > 0)
        {
            return new ReadingState(this.pages, newNumber);
        }

        return this;
    }
}
