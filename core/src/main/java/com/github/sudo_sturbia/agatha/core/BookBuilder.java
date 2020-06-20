package com.github.sudo_sturbia.agatha.core;

/**
 * BookBuilder is used to create Book objects!
 * <p>
 * BookBuilder provides a number of methods that allow specification
 * of properties of Book object to be created. Each BookBuilder
 * holds the state of a Book. When using BookBuilder the first
 * method to be called should always be <code>newBook</code>, with
 * the last method being <code>build</code>.
 * <p>
 * Usage:
 * <pre>
 * Book book = BookBuilder.newBook()
 *                        .name("Book's name")
 *                        .numberOfPages(100)
 *                        .state(BookState.State.READ)
 *                        .author("Author's name")
 *                        .coverPath("/path/to/cover")
 *                        .build();
 * </pre>
 */
public class BookBuilder
{
    /** Book's name. */
    private String name;

    /** Name of Book's author. */
    private String author;

    /** Book's current state. */
    private BookState.State state;

    /** Number of book's pages. */
    private int pages;

    /** Path to book's cover image. */
    private String coverPath;

    /** Private constructor. */
    private BookBuilder() { }

    /**
     * Create a BookBuilder object with no fields.
     *
     * @return Empty BookBuilder object.
     */
    public static BookBuilder newBook()
    {
        return new BookBuilder();
    }

    /**
     * Create a BookBuilder object with the specified name and
     * number of pages. Specified fields are used when creating
     * a Book object.
     *
     * @param name name of the book.
     * @param numberOfPages number of book's pages.
     * @return A BookBuilder object with the specified fields.
     */
    public static BookBuilder newBook(String name, int numberOfPages)
    {
        BookBuilder builder = new BookBuilder();

        builder.name = name;
        builder.pages = numberOfPages;

        return builder;
    }

    /**
     * Build and return a Book object containing fields specified
     * in BookBuilder.
     * <p>
     * Of Book's fields Name and number of pages are required to
     * be specified. State can be unspecified, in which case it's
     * considered "interested". Cover path and author can be left
     * without specification.
     *
     * @return A newly created Book object.
     * @throws IllegalArgumentException if name is null or number of
     *         pages is &lt;= zero.
     */
    public Book build() throws IllegalArgumentException
    {
        if (this.name == null)
        {
            throw new IllegalArgumentException("Name is not specified.");
        }
        else if (this.pages <= 0)
        {
            throw new IllegalArgumentException("Invalid number of pages.");
        }
        else if (this.state == null)
        {
            this.state = BookState.State.INTERESTED;
        }

        Book book = new BookImp(this.name, this.pages);

        book.setAuthor(this.author);
        book.setState(this.state);
        book.setCoverImage(coverPath);

        return book;
    }

    /**
     * Set Book's name.
     *
     * @param name book's name.
     * @return A BookBuilder object with Book's name specified.
     */
    public BookBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    /**
     * Set author's name.
     *
     * @param author author's name.
     * @return A BookBuilder object with author specified.
     */
    public BookBuilder author(String author)
    {
        this.author = author;
        return this;
    }

    /**
     * Set book's state.
     *
     * @param state book's state.
     * @return A BookBuilder object with state specified.
     */
    public BookBuilder state(BookState.State state)
    {
        this.state = state;
        return this;
    }

    /**
     * Set book's number of pages.
     *
     * @param pages number of book's pages.
     * @return A BookBuilder object with number of pages specified.
     */
    public BookBuilder numberOfPages(int pages)
    {
        this.pages = pages;
        return this;
    }

    /**
     * Set path to book's cover.
     *
     * @param coverPath path to book's cover.
     * @return A BookBuilder object with cover's path specified.
     */
    public BookBuilder coverPath(String coverPath)
    {
        this.coverPath = coverPath;
        return this;
    }
}
