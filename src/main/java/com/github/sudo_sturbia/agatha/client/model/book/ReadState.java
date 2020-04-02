package com.github.sudo_sturbia.agatha.client.model.book;

/**
 * ReadState is one of three possible states a book can be in.
 */
public class ReadState implements BookState
{
    /** Containing book. */
    private Book book;

    /**
     * ReadState's constructor.
     *
     * @param book the book containing the state.
     * @throws IllegalArgumentException if Book is null.
     */
    public ReadState(Book book) throws IllegalArgumentException
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
        return State.READ;
    }

    @Override
    public String  getStateToString()
    {
        return "read";
    }

    @Override
    public int getNumberOfReadPages()
    {
        return this.book.getNumberOfPages();
    }

    @Override
    public BookState updateNumberOfReadPages(int newNumber) throws IllegalArgumentException
    {
        if (newNumber < 0 || newNumber > this.book.getNumberOfPages())
        {
            throw new IllegalArgumentException("Invalid number of pages.");
        }

        // Transition to "currently-reading"
        if (newNumber < this.book.getNumberOfPages())
        {
            return new ReadingState(this.book, newNumber);
        }

        return this;
    }
}
