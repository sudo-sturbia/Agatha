package com.github.sudo_sturbia.agatha.server.request;

import com.github.sudo_sturbia.agatha.client.model.book.Book;
import com.github.sudo_sturbia.agatha.client.model.book.BookBuilder;
import com.github.sudo_sturbia.agatha.client.model.book.BookState;
import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Read handles client's READ requests. READ is used to retrieve
 * client's data. An empty READ can also be used to verify
 * client's credentials.
 * <P>
 * A READ request can be one of the following:
 * <P>
 * <pre>
 *     READ username:password             // Verify client's credentials.
 *     READ username:password/b/bookName  // Get book.
 *     READ username:password/b/*         // Get a list of names all user's books.
 *     READ username:password/l/labelName // Get a list of names all books with label.
 * </pre>
 */
public class Read implements Request
{
    /** String representing request. */
    private final String request;

    /** Name of application's database. */
    private final String dbName;

    /**
     * Read's constructor. Used only by RequestBuilder.
     *
     * @param request request to handle.
     * @param dbName name of application's database.
     */
    Read(final String request, final String dbName)
    {
        this.request = request;
        this.dbName = dbName;
    }

    /**
     * Handles the request and returns a JSON object.
     *
     * @return Requested JSON object if request is correct, an
     *         ExecutionState object otherwise.
     */
    @Override
    public String handle()
    {
        if (this.isReadUser())
        {
            return this.readUser();
        }
        else if (this.isReadBook())
        {
            return this.readBook();
        }
        else if (this.isReadBooksNames())
        {
            return this.readBooksNames();
        }
        else if (this.isReadBooksWithLabel())
        {
            return this.readBooksWithLabel();
        }
        else
        {
            return new Gson().toJson(new ExecutionState(1)); // Wrong syntax
        }
    }

    /**
     * Check if given request string is correct.
     *
     * @return True if request is correct, false otherwise.
     */
    private boolean isCorrect()
    {
        return Pattern.compile("^READ\\s+[^:]+:[^:/]+(/(b/([^/]+|\\*)|l/[^/]+)|)$",
                Pattern.CASE_INSENSITIVE).matcher(request).matches();
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     READ username:password
     * </pre>
     *
     * @return True if read user statement, false otherwise.
     */
    private boolean isReadUser()
    {
        return Pattern.compile("^READ\\s+[^:]+:[^:/]+$", Pattern.CASE_INSENSITIVE)
                .matcher(request).matches();
    }

    /**
     * Verifies a client's credentials. Returns an execution state
     * object depending on the credentials.
     *
     * @return A JSON response.
     */
    private String readUser()
    {
        String[] list = this.request.split("^READ\\s+|:");
        if (list.length != 2)
        {
            return new Gson().toJson(new ExecutionState(3)); // Operation failed
        }

        // Verify username and password
        if (!UserManager.doesExist(this.dbName, list[0], list[1]))
        {
            return new Gson().toJson(new ExecutionState(2)); // Wrong credentials
        }

        return new Gson().toJson(new ExecutionState(0)); // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     READ username:password/b/bookName
     * </pre>
     *
     * @return True if read book statement, false otherwise.
     */
    private boolean isReadBook()
    {
        return Pattern.compile("^READ\\s+[^:]+:[^:/]+/b/[^/]+$", Pattern.CASE_INSENSITIVE)
                .matcher(request).matches();
    }

    /**
     * Load a book from user's library. Returns a JSON
     * representation of the book.
     *
     * @return A JSON response.
     */
    private String readBook()
    {
        String[] list = this.request.split("^READ\\s+|:|/b/");
        if (list.length != 3)
        {
            return new Gson().toJson(new ExecutionState(3)); // Operation failed
        }

        // Verify username and password
        if (!UserManager.doesExist(this.dbName, list[0], list[1]))
        {
            return new Gson().toJson(new ExecutionState(2)); // Wrong credentials
        }

        return this.loadBook(list[2], list[0]);
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     READ username:password/b/*
     * </pre>
     *
     * @return True if read books' names statement, false otherwise.
     */
    private boolean isReadBooksNames()
    {
        return Pattern.compile("^READ\\s+[^:]+:[^:/]+/b/\\*$", Pattern.CASE_INSENSITIVE)
                .matcher(request).matches();
    }

    /**
     * Return a JSON list containing names of all books existing
     * in user's table.
     *
     * @return A JSON response.
     */
    private String readBooksNames()
    {
        String[] list = this.request.split("^READ\\s+|:|/b/\\*$");
        if (list.length != 2)
        {
            return new Gson().toJson(new ExecutionState(3)); // Operation failed
        }

        // Verify username and password
        if (!UserManager.doesExist(this.dbName, list[0], list[1]))
        {
            return new Gson().toJson(new ExecutionState(2)); // Wrong credentials
        }

        return this.loadBooksNames(list[0]);
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     READ username:password/l/labelName
     * </pre>
     *
     * @return True if read books with label statement, false otherwise.
     */
    private boolean isReadBooksWithLabel()
    {
        return Pattern.compile("^READ\\s+[^:]+:[^:/]+/l/[^/]+$", Pattern.CASE_INSENSITIVE)
                .matcher(request).matches();
    }

    /**
     * Return a JSON list containing names of books with given
     * label.
     *
     * @return A JSON response.
     */
    private String readBooksWithLabel()
    {
        String[] list = this.request.split("^READ\\s+|:|/b/\\*$");
        if (list.length != 3)
        {
            return new Gson().toJson(new ExecutionState(3)); // Operation failed
        }

        // Verify username and password
        if (!UserManager.doesExist(this.dbName, list[0], list[1]))
        {
            return new Gson().toJson(new ExecutionState(2)); // Wrong credentials
        }

        return this.loadBooksNamesWithLabel(list[2], list[0]);
    }

    /**
     * Load a book from user's table.
     *
     * @param bookName name of book to load.
     * @param username user's username.
     * @return A JSON object (Book or ExecutionState).
     */
    private String loadBook(String bookName, String username)
    {
        try (
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement getBook = connection.prepareStatement(
                        "SELECT * FROM ?.? WHERE bookName=?"
                );
                PreparedStatement getNotes = connection.prepareStatement(
                        "SELECT * FROM ?.?"
                );
        ) {
            getBook.setString(1, this.dbName);
            getBook.setString(2, username);
            getBook.setString(3, bookName);

            ResultSet set = getBook.executeQuery();
            if (set.next())
            {
                BookState.State state;
                switch (set.getString("state"))
                {
                    case "reading":
                        state = BookState.State.CURRENTLY_READING;
                        break;
                    case "read":
                        state = BookState.State.READ;
                        break;
                    case "interested":
                        state = BookState.State.INTERESTED;
                        break;
                    default:
                        // Can't happen
                        state = null;
                }

                Book book = BookBuilder.newBook()
                                       .name(set.getString("bookName"))
                                       .author(set.getString("author"))
                                       .state(state)
                                       .numberOfPages(set.getInt("pages"))
                                       .coverPath(set.getString("coverPath"))
                                       .build();

                // Load user's notes
                if (set.getBoolean("hasNotes"))
                {
                    getNotes.setString(1, this.dbName);
                    getNotes.setString(2, username + bookName);

                    ResultSet noteSet = getNotes.executeQuery();
                    while (noteSet.next())
                    {
                        book.addNote(noteSet.getString("note"), noteSet.getInt("page"));
                    }
                }

                return new Gson().toJson(book);
            }
        }
        catch (SQLException e)
        {
            // Operation failed
        }

        return new Gson().toJson(new ExecutionState(3)); // Operation failed
    }

    /**
     * Return a JSON list containing names of all user's books.
     *
     * @param username user's username.
     * @return A JSON object.
     */
    private String loadBooksNames(String username)
    {
        try (
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement getNames = connection.prepareStatement(
                        "SELECT bookName FROM ?.?"
                );
        ) {
            getNames.setString(1, this.dbName);
            getNames.setString(2, username);

            List<String> bookNames = new ArrayList<>();

            ResultSet set = getNames.executeQuery();
            while (set.next())
            {
                bookNames.add(set.getString("bookName"));
            }

            return new Gson().toJson(bookNames);
        }
        catch (SQLException e)
        {
            // Operation failed
        }

        return new Gson().toJson(new ExecutionState(3)); // Operation failed
    }

    /**
     * Return a JSON list containing names of user's books
     * with given label.
     *
     * @param label name of label.
     * @param username user's username.
     * @return A JSON list or ExecutionState in case of failure.
     */
    private String loadBooksNamesWithLabel(String label, String username)
    {
        try (
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement getNames = connection.prepareStatement(
                        "SELECT bookName FROM ?.? WHERE ? = 1"
                );
        ) {
            getNames.setString(1, this.dbName);
            getNames.setString(2, username);
            getNames.setString(3, label);

            List<String> bookNames = new ArrayList<>();

            ResultSet names = getNames.executeQuery();
            while (names.next())
            {
                bookNames.add(names.getString("bookName"));
            }

            return new Gson().toJson(bookNames);
        }
        catch (SQLException e)
        {
            // Operation failed
        }

        return new Gson().toJson(new ExecutionState(3)); // Operation failed
    }
}
