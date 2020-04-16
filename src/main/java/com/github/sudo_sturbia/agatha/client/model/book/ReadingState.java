package com.github.sudo_sturbia.agatha.client.model.book;

/**
 * ReadingState is one of three possible states a book can be in.
 */
public class ReadingState implements BookState
{
    /** Containing book. */
    private Book book;

    /** Number of book's read pages. */
    private int readPages;

    /**
     * ReadingState's constructor.
     *
     * @param book book containing state.
     * @param readPages number of book's read pages.
     * @throws IllegalArgumentException if book is null or
     *         readPages >= number of book's pages of < 0.
     */
    ReadingState(Book book, int readPages) throws IllegalArgumentException
    {
        if (book == null)
        {
            throw new IllegalArgumentException("No book is given.");
        }
        else if (readPages < 0 || readPages >= book.getNumberOfPages())
        {
            throw new IllegalArgumentException("Invalid number of read pages.");
        }

        this.book = book;
        this.readPages = readPages;
    }

    @Override
    public State getState()
    {
        return State.CURRENTLY_READING;
    }

    @Override
    public String getStateToString()
    {
        return "reading";
    }

    @Override
    public int getNumberOfReadPages()
    {
        return this.readPages;
    }

    @Override
    public BookState setNumberOfReadPages(int newNumber) throws IllegalArgumentException
    {
        if (newNumber < 0 || newNumber > this.book.getNumberOfPages())
        {
            throw new IllegalArgumentException("Invalid number of read pages.");
        }

        // Transition to "read" if book is finished
        if (newNumber == this.book.getNumberOfPages())
        {
            return new ReadState(this.book);
        }

        this.readPages = newNumber;
        return this;
    }
}
