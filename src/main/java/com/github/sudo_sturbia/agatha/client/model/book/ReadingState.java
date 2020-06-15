package com.github.sudo_sturbia.agatha.client.model.book;

/**
 * ReadingState is one of three possible states a book can be in.
 */
public class ReadingState implements BookState
{
    /** Number of book's pages. */
    private final int pages;

    /** Number of book's read pages. */
    private int readPages;

    /**
     * ReadingState's constructor.
     *
     * @param pages number of book's pages.
     * @param readPages number of book's read pages.
     * @throws IllegalArgumentException if pages < 0 or
     *         readPages >= number of book's pages of < 0.
     */
    ReadingState(int pages, int readPages) throws IllegalArgumentException
    {
        if (pages < 0)
        {
            throw new IllegalArgumentException("Invalid number of pages.");
        }
        else if (readPages < 0 || readPages >= pages)
        {
            throw new IllegalArgumentException("Invalid number of read pages.");
        }

        this.pages = pages;
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
        if (newNumber < 0 || newNumber > this.pages)
        {
            throw new IllegalArgumentException("Invalid number of read pages.");
        }

        // Transition to "read" if book is finished
        if (newNumber == this.pages)
        {
            return new ReadState(this.pages);
        }

        this.readPages = newNumber;
        return this;
    }
}
