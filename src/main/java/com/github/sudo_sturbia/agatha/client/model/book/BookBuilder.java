package com.github.sudo_sturbia.agatha.client.model.book;

import java.util.Random;

/**
 * BookBuilder is used for creation of Book objects.
 * BookBuilder contains only static build methods. Each method
 * creates a Book object with different properties. Created
 * books are guaranteed to have unique IDs.
 */
public class BookBuilder
{
    /**
     * Builds and returns a Book object with a unique ID, in
     * default state (interested), and with no cover image.
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
     * Builds and returns a Book object with a unique ID, in
     * default state (interested).
     *
     * @param name book's name.
     * @param pages number of book's pages.
     * @param coverPath path to cover image.
     * @return a built book object.
     * @throws IllegalArgumentException if a given parameter is invalid.
     */
    public static Book build(String name, int pages, String coverPath) throws IllegalArgumentException
    {
        return new BookImp(name, BookBuilder.generateID(), pages, coverPath);
    }

    /**
     * Builds and returns a Book object with a unique ID, and
     * with no cover image.
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
     * Builds and returns a Book object with a unique ID.
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
        Book book = new BookImp(name, BookBuilder.generateID(), pages, coverPath);
        book.updateState(state);

        return book;
    }

    /**
     * Generate a unique book ID with to guarantee that it does
     * not exist in the database. Generated ID is 8 characters long.
     *
     * @return generated ID string.
     */
    private static String generateID()
    {
        // Create a random object
        Random random = new Random(System.currentTimeMillis());

        // Generate a number and test it
        StringBuilder stringBuilder = new StringBuilder();
        do {
            for (int i = 0; i < 8; i++)
            {
                stringBuilder.append((char) random.nextInt());
            }
        } while (!BookBuilder.isIDUnique(stringBuilder.toString()));

        return stringBuilder.toString();
    }

    /**
     * Query database to test if ID is unique.
     *
     * @return true if ID is unique, false otherwise.
     */
    private static boolean isIDUnique(String ID)
    {
        // TODO ..
        return ID != null;
    }
}
