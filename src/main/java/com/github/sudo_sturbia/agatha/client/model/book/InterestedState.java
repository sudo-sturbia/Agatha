package com.github.sudo_sturbia.agatha.client.model.book;

/**
 * InterestedState is one of three possible states a book can be in.
 */
public class InterestedState implements BookState
{
    /** Containing book. */
    private Book book;

    /**
     * InterestedState's constructor.
     *
     * @param book book containing state.
     * @throws IllegalArgumentException if book is null.
     */
    public InterestedState(Book book) throws IllegalArgumentException
    {
        if (book == null)
        {
            throw new IllegalArgumentException("No book is given.");
        }

        this.book = book;
    }

    @Override
    public State getState()
    {
        return State.INTERESTED;
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
    public BookState updateNumberOfReadPages(int newNumber) throws IllegalArgumentException
    {
        if (newNumber < 0 || newNumber > this.book.getNumberOfPages())
        {
            throw new IllegalArgumentException("Invalid number of read pages.");
        }

        // Transition to "read"
        if (newNumber == this.book.getNumberOfPages())
        {
            return new ReadState(this.book);
        }
        // Transition to "currently reading"
        else if (newNumber > 0)
        {
            return new ReadingState(this.book, newNumber);
        }

        return this;
    }
}
