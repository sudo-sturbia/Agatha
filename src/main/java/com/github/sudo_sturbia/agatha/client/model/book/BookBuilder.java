package com.github.sudo_sturbia.agatha.client.model.book;

/**
 * BookBuilder is used for creation of Book objects.
 * BookBuilder contains only static build methods. Each method
 * creates a Book object with different properties.
 */
public class BookBuilder
{
    /**
     * Builds and returns a Book object in default state (interested),
     * and with no cover image.
     *
     * @param name book's name.
     * @param pages number of book's pages.
     * @return a built book object.
     * @throws IllegalArgumentException if a given parameter is invalid.
     */
    public static Book build(String name, int pages) throws IllegalArgumentException
    {
        return BookBuilder.build(name, pages, null);
    }

    /**
     * Builds and returns a Book object in default state (interested).
     *
     * @param name book's name.
     * @param pages number of book's pages.
     * @param coverPath path to cover image.
     * @return a built book object.
     * @throws IllegalArgumentException if a given parameter is invalid.
     */
    public static Book build(String name, int pages, String coverPath) throws IllegalArgumentException
    {
        return new BookImp(name, pages, coverPath);
    }

    /**
     * Builds and returns a Book object with no cover image.
     *
     * @param name book's name.
     * @param state book's current state.
     * @param pages number of book's pages.
     * @return a built book object.
     * @throws IllegalArgumentException if a given parameter is invalid.
     */
    public static Book build(String name, BookState.State state, int pages) throws IllegalArgumentException
    {
        return BookBuilder.build(name, state, pages, null);
    }

    /**
     * Builds and returns a Book object.
     *
     * @param name book's name.
     * @param state book's current state.
     * @param pages number of book's pages.
     * @param coverPath path to cover image.
     * @return a built book object.
     * @throws IllegalArgumentException if a given parameter is invalid.
     */
    public static Book build(String name, BookState.State state, int pages, String coverPath) throws IllegalArgumentException
    {
        // Create a Book object and update it's state
        Book book = new BookImp(name, pages, coverPath);
        book.setState(state);

        return book;
    }
}
