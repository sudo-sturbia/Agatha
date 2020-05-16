package com.github.sudo_sturbia.agatha.server.request;

import com.github.sudo_sturbia.agatha.client.model.book.Book;
import com.github.sudo_sturbia.agatha.client.model.book.BookImp;
import com.github.sudo_sturbia.agatha.client.model.book.Note;
import com.github.sudo_sturbia.agatha.client.model.book.NoteImp;
import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Create handles client's CREATE requests. CREATE is used to
 * create users, books, labels, or notes.
 * <P>
 * A CREATE request can be one of the following:
 * <pre>
 *     CREATE username:password                     // Creates a new application user.
 *     CREATE username:password/b/{JSON}            // Creates a new book from the given JSON object.
 *     CREATE username:password/b/bookName/n/{JSON} // Creates a new note from the given JSON object.
 *     CREATE username:password/l/labelName         // Creates a new label with no books.
 * </pre>
 */
public class Create implements Request
{
    /** String representing request. */
    private final String request;

    /** Name of application's database. */
    private final String dbName;

    /**
     * Create's constructor. Used only by RequestBuilder.
     *
     * @param request request to handle.
     * @param dbName name of application's database.
     */
    Create(final String request, final String dbName)
    {
        this.request = request;
        this.dbName = dbName;
    }

    /**
     * Handles the request and returns an ExecutionState object
     * in JSON.
     *
     * @return A JSON ExecutionState object.
     */
    @Override
    public String handle()
    {
        // Handle request based on it's type
        if (this.isCreateUser())
        {
            return this.createUser();
        }
        else if (this.isCreateBook())
        {
            return this.createBook();
        }
        else if (this.isCreateNote())
        {
            return this.createNote();
        }
        else if (this.isCreateLabel())
        {
            return this.createLabel();
        }
        else
        {
            return new Gson().toJson(new ExecutionState(1)); // Wrong syntax
        }
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     CREATE username:password
     * </pre>
     *
     * @return True if create user statement, false otherwise.
     */
    private boolean isCreateUser()
    {
        return Pattern.compile("^CREATE\\s+[^:]+:[^:/]+$", Pattern.CASE_INSENSITIVE)
                .matcher(request).matches();
    }

    /**
     * Creates a new application user.
     *
     * @return A JSON response.
     */
    private String createUser()
    {
        // Break statement into username and password
        String[] list = this.request.split("^CREATE\\s+|:");

        if (list.length != 2)
        {
            return new Gson().toJson(new ExecutionState(3)); // Operation failed
        }

        // Verify that username doesn't already exist
        if (UserManager.doesExist(this.dbName, list[0]))
        {
            return new Gson().toJson(new ExecutionState(3)); // Operation failed
        }

        if (!this.writeUser(list[0], DigestUtils.sha256Hex(list[1])))
        {
            return new Gson().toJson(new ExecutionState(3)); // Operation failed
        }

        return new Gson().toJson(new ExecutionState(0)); // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     CREATE username:password/b/{JSON}
     * </pre>
     *
     * @return True if create book statement, false otherwise.
     */
    private boolean isCreateBook()
    {
        return Pattern.compile("^CREATE\\s+[^:]+:[^:/]+/b/\\{.+}$", Pattern.CASE_INSENSITIVE)
                .matcher(request).matches();
    }

    /**
     * Creates a new book for given user.
     *
     * @return A JSON response.
     */
    private String createBook()
    {
        // Used for unmarshalling of JSON
        Gson gson = new Gson();

        String[] list = this.request.split("^CREATE\\s+|:|/b/");
        if (list.length != 3)
        {
            return gson.toJson(new ExecutionState(3)); // Operation failed
        }

        // Verify username and password
        if (!UserManager.doesExist(this.dbName, list[0], list[1]))
        {
            return gson.toJson(new ExecutionState(2)); // Wrong credentials
        }

        Book book;
        try
        {
            book = gson.fromJson(list[2], BookImp.class);
        }
        catch (JsonSyntaxException e)
        {
            // JSON can't be unmarshalled
            return gson.toJson(new ExecutionState(3)); // Operation failed
        }

        if (!this.writeBook(book, list[0]))
        {
            return gson.toJson(new ExecutionState(3)); // Operation failed
        }

        return gson.toJson(new ExecutionState(0)); // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     CREATE username:password/b/bookName/n/{JSON}
     * </pre>
     *
     * @return True if create note statement, false otherwise.
     */
    private boolean isCreateNote()
    {
        return Pattern.compile("^CREATE\\s+[^:]+:[^:/]+/b/[^/]+/n/\\{.+}$", Pattern.CASE_INSENSITIVE)
                .matcher(request).matches();
    }

    /**
     * Creates a new note for given user.
     *
     * @return A JSON response.
     */
    private String createNote()
    {
        // Used for unmarshalling JSON
        Gson gson = new Gson();

        String[] list = this.request.split("^CREATE\\s+|:|/b/|/n/");
        if (list.length != 4)
        {
            return gson.toJson(new ExecutionState(3)); // Operation failed
        }

        // Verify username and password
        if (!UserManager.doesExist(this.dbName, list[0], list[1]))
        {
            return gson.toJson(new ExecutionState(2)); // Wrong credentials
        }

        Note note;
        try
        {
            note = gson.fromJson(list[3], NoteImp.class);
        }
        catch (JsonSyntaxException e)
        {
            // JSON can't be unmarshalled
            return gson.toJson(new ExecutionState(3)); // Operation failed
        }

        if (!this.writeNote(note, list[0], list[2]))
        {
            return gson.toJson(new ExecutionState(3)); // Operation failed
        }

        return gson.toJson(new ExecutionState(0)); // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     CREATE username:password/l/labelName
     * </pre>
     *
     * @return True if create label statement, false otherwise.
     */
    private boolean isCreateLabel()
    {
        return Pattern.compile("^CREATE\\s+[^:]+:[^:/]+/l/[^/]+$", Pattern.CASE_INSENSITIVE)
                .matcher(request).matches();
    }

    /**
     * Creates a new empty label for given user.
     *
     * @return A JSON response.
     */
    private String createLabel()
    {
        String[] list = this.request.split("^CREATE\\s+|:|/l/");
        if (list.length != 3)
        {
            return new Gson().toJson(new ExecutionState(3)); // Operation failed
        }

        // Verify username and password
        if (!UserManager.doesExist(this.dbName, list[0], list[1]))
        {
            return new Gson().toJson(new ExecutionState(2)); // Wrong credentials
        }

        // Add a new boolean column to user's books table.
        // Default value is false.
        try (
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement checkColumn = connection.prepareStatement(
                        "SHOW COLUMNS FROM ?.? like '?';"
                );
                PreparedStatement addColumn = connection.prepareStatement(
                        "ALTER TABLE ?.? ADD ? bool NOT NULL DEFAULT 0;"
                );
        ) {
            checkColumn.setString(1, this.dbName);
            checkColumn.setString(2, list[0]); // username
            checkColumn.setString(3, list[2]); // label

            ResultSet set = checkColumn.executeQuery();
            if (set.next()) // Column already exists
            {
                return new Gson().toJson(new ExecutionState(3)); // Operation failed
            }

            addColumn.setString(1, this.dbName);
            addColumn.setString(2, list[0]); // username
            addColumn.setString(3, list[2]); // label

            addColumn.executeUpdate();
        }
        catch (SQLException e)
        {
            return new Gson().toJson(new ExecutionState(3)); // Operation failed
        }

        return new Gson().toJson(new ExecutionState(0)); // Successful
    }

    /**
     * Saves a new user's credentials to Users table. Creates a
     * books' table for the user.
     *
     * @param username user's username.
     * @param hashedPass user's password hashed using sha256.
     * @return True if operation is performed successfully, false otherwise.
     */
    private boolean writeUser(String username, String hashedPass)
    {
        try (
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement addUser = connection.prepareStatement(
                        "INSERT INTO ?.Users VALUES ('?', '?');"
                );
                PreparedStatement createTable = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS ?.? (" +
                                "bookName varchar(255) NOT NULL, " +
                                "author varchar(255), " +
                                "state varchar(10) NOT NULL, " +
                                "pages int(255) NOT NULL, " +
                                "readPages int(255), " +
                                "coverPath varchar(255), " +
                                "hasNotes bool, " +
                                "PRIMARY KEY(bookName)" +
                                ");"
                );
        ) {
            addUser.setString(1, this.dbName);
            addUser.setString(2, username);
            addUser.setString(3, hashedPass);

            createTable.setString(1, this.dbName);
            createTable.setString(2, username);

            addUser.executeUpdate();
            createTable.executeUpdate();
        }
        catch (SQLException e)
        {
            return false;
        }

        return true;
    }

    /**
     * Writes a Book object to a user's table. Verifies that book's
     * name doesn't already exist in the table, returns false if it
     * does exist.
     *
     * @param book book to write to table.
     * @param username name of current user (also name of user's table.)
     * @return True if operation performed successfully, false otherwise.
     */
    private boolean writeBook(Book book, String username)
    {
        // Verifies that book's name doesn't already exist.
        // Writes book's fields to user's table, and writes all
        // of book's notes (if any exist) to book's table.
        try (
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement checkBook = connection.prepareStatement(
                        "SELECT * FROM ?.? WHERE bookName=?;"
                );
                PreparedStatement addBook = connection.prepareStatement(
                        "INSERT INTO ?.? VALUES" +
                                "('?', '?', '?', ?, ?, '?', ?);"
                );
                PreparedStatement createNotesTable = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS ?.? (" +
                                "note varchar(65535), " +
                                "page int(255) NOT NULL, " +
                                "PRIMARY KEY(page)" +
                                ");"
                )
        ) {
            // Verify that book's name doesn't already exist
            checkBook.setString(1, this.dbName);
            checkBook.setString(2, username);
            checkBook.setString(3, book.getName());
            try (ResultSet set = checkBook.executeQuery())
            {
                // Book's name already exists
                if (set.next())
                {
                    return false;
                }
            }

            // Insert book
            addBook.setString(1, this.dbName);
            addBook.setString(2, username);
            addBook.setString(3, book.getName());
            addBook.setString(4, book.getAuthor());
            addBook.setString(5, book.getStateToString());
            addBook.setInt(6, book.getNumberOfPages());
            addBook.setInt(7, book.getNumberOfReadPages());
            addBook.setString(8, book.getCoverImagePath());
            addBook.setBoolean(9, book.getNotes().size() > 0);

            addBook.executeUpdate();

            // Create notes table
            createNotesTable.setString(1, this.dbName);
            createNotesTable.setString(2, username + book.getName());

            createNotesTable.executeUpdate();

            if (!this.writeNotes(book.getNotes(), username, book.getName()))
            {
                return false;
            }
        }
        catch (SQLException e)
        {
            return false;
        }

        return true;
    }

    /**
     * Writes a list of notes to user's book's table.
     *
     * @param notes notes to write to table.
     * @param username user's username.
     * @param bookName name of the book containing the note.
     * @return True if operation is performed successfully, false otherwise.
     */
    private boolean writeNotes(List<Note> notes, String username, String bookName)
    {
        try (
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement insertNote = connection.prepareStatement(
                        "INSERT INTO ?.? VALUES ('?', ?);"
                )
        ) {
            insertNote.setString(1, this.dbName);
            insertNote.setString(2, username + bookName);

            for (Note note : notes)
            {
                insertNote.setString(3, note.getNote());
                insertNote.setInt(4, note.getPageNumber());

                insertNote.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            return false;
        }

        return true;
    }

    /**
     * Write note to user's table. Verifies that the given book
     * name exists and that given page is not already in use,
     * returns false if it is.
     *
     * @param note note to write
     * @param username user's username.
     * @param bookName name of the book containing the note.
     * @return True if operation is performed successfully, false otherwise.
     */
    private boolean writeNote(Note note, String username, String bookName)
    {
        try (
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement checkBookName = connection.prepareStatement(
                        "SELECT * FROM ?.? WHERE bookName=?;"
                );
                PreparedStatement checkPage = connection.prepareStatement(
                        "SELECT * FROM ?.? WHERE page=?;"
                );
                PreparedStatement insertNote = connection.prepareStatement(
                        "INSERT INTO ?.? VALUES ('?', ?);"
                )
        ) {
            checkBookName.setString(1, this.dbName);
            checkBookName.setString(2, username);
            checkBookName.setString(3, bookName);

            ResultSet book = checkBookName.executeQuery();
            if (!book.next()) // Book's name doesn't exist
            {
                return false;
            }

            checkPage.setString(1, this.dbName);
            checkPage.setString(2, username + bookName);
            checkPage.setInt(3, note.getPageNumber());

            ResultSet set = checkPage.executeQuery();
            if (set.next()) // Page's number is used
            {
                return false;
            }

            insertNote.setString(1, this.dbName);
            insertNote.setString(2, username+bookName);
            insertNote.setString(3, note.getNote());
            insertNote.setInt(4, note.getPageNumber());

            insertNote.executeUpdate();
        }
        catch (SQLException e)
        {
            return false;
        }

        return true;
    }
}
