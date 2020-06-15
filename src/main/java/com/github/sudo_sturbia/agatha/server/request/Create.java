package com.github.sudo_sturbia.agatha.server.request;

import com.github.sudo_sturbia.agatha.client.model.book.Book;
import com.github.sudo_sturbia.agatha.client.model.book.BookImp;
import com.github.sudo_sturbia.agatha.client.model.book.Note;
import com.github.sudo_sturbia.agatha.client.model.book.NoteImp;
import com.github.sudo_sturbia.agatha.server.clients.ClientManager;
import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
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

        return new Gson().toJson(new ExecutionState(1)); // Wrong syntax
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
                .matcher(this.request).matches();
    }

    /**
     * Creates a new application user.
     *
     * @return A JSON response.
     */
    private String createUser()
    {
        String[] list = RequestUtil.removeEmpty(this.request.split("^CREATE\\s+|:"));
        return list.length != 2 || ClientManager.get().doesExist(this.dbName, list[0]) || !this.writeUser(list[0], list[1]) ?
                new Gson().toJson(new ExecutionState(3)) : // Operation failed
                new Gson().toJson(new ExecutionState(0)); // Successful
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
                .matcher(this.request).matches();
    }

    /**
     * Creates a new book for given user.
     *
     * @return A JSON response.
     */
    private String createBook()
    {
        String[] list = RequestUtil.removeEmpty(this.request.split("^CREATE\\s+|:|/b/"));

        String state;
        if ((state = RequestUtil.verify(this.dbName, list, 3)) != null)
        {
            return state;
        }

        Book book;
        Gson gson = new Gson();
        try {
            book = gson.fromJson(list[2], BookImp.class);
        }
        catch (JsonSyntaxException e) {
            return gson.toJson(new ExecutionState(3)); // JSON can't be unmarshalled
        }

        return !this.writeBook(book, list[0]) ?
                gson.toJson(new ExecutionState(3)) : // Operation failed
                gson.toJson(new ExecutionState(0));  // Successful
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
                .matcher(this.request).matches();
    }

    /**
     * Creates a new note for given user.
     *
     * @return A JSON response.
     */
    private String createNote()
    {
        String[] list = RequestUtil.removeEmpty(this.request.split("^CREATE\\s+|:|/b/|/n/"));

        String state;
        if ((state = RequestUtil.verify(this.dbName, list, 4)) != null)
        {
            return state;
        }

        Note note;
        Gson gson = new Gson();
        try {
            note = gson.fromJson(list[3], NoteImp.class);
        }
        catch (JsonSyntaxException e) {
            return gson.toJson(new ExecutionState(3)); // JSON can't be unmarshalled
        }

        return !this.writeNote(note, list[0], list[2]) ?
                gson.toJson(new ExecutionState(3)) : // Operation failed
                gson.toJson(new ExecutionState(0));  // Successful
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
                .matcher(this.request).matches();
    }

    /**
     * Creates a new empty label for given user.
     *
     * @return A JSON response.
     */
    private String createLabel()
    {
        String[] list = RequestUtil.removeEmpty(this.request.split("^CREATE\\s+|:|/l/"));

        String state;
        return (state = RequestUtil.verify(this.dbName, list, 3)) != null ?
                state :
                !this.writeLabel(list[0], list[2]) ?
                        new Gson().toJson(new ExecutionState(3)) : // Operation failed
                        new Gson().toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Saves a new user's credentials to Users table. Creates a
     * books' table for the user.
     *
     * @param username user's username.
     * @param password user's password.
     * @return True if operation is performed successfully, false otherwise.
     */
    private boolean writeUser(String username, String password)
    {
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement addUser = connection.prepareStatement(
                        "INSERT INTO " + this.dbName + ".Users VALUES (?, ?, ?);"
                );
                PreparedStatement createTable = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS " + this.dbName + "." + username + " (" +
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
            String salt = this.salt();

            addUser.setString(1, username);
            addUser.setString(2, DigestUtils.sha256Hex(password + salt));
            addUser.setString(3, salt);
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
     * Create a 16 character salt.
     *
     * @return A 16 character random string to be used as a salt.
     */
    private String salt()
    {
        char[] possibleChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 16; i++)
        {
            builder.append(possibleChars[random.nextInt(possibleChars.length)]);
        }

        return builder.toString();
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
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement checkBook = connection.prepareStatement(
                        "SELECT * FROM " + this.dbName + "." + username + " WHERE bookName = ?;"
                );
                PreparedStatement addBook = connection.prepareStatement(
                        "INSERT INTO " + this.dbName + "." + username + " VALUES" +
                                "(?, ?, ?, ?, ?, ?, ?);"
                );
                PreparedStatement createNotesTable = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS " + this.dbName + "." + username + book.getName() + " (" +
                                "note varchar(65535), " +
                                "page int(255) NOT NULL, " +
                                "PRIMARY KEY(page)" +
                                ");"
                )
        ) {
            // Verify that book's name doesn't already exist
            checkBook.setString(1, book.getName());
            try (ResultSet set = checkBook.executeQuery())
            {
                // Book's name already exists
                if (set.next())
                {
                    return false;
                }
            }

            // Insert book
            addBook.setString(1, book.getName());
            addBook.setString(2, book.getAuthor());
            addBook.setString(3, book.getStateToString());
            addBook.setInt(4, book.getNumberOfPages());
            addBook.setInt(5, book.getNumberOfReadPages());
            addBook.setString(6, book.getCoverImagePath());
            addBook.setBoolean(7, book.getNotes().size() > 0);

            addBook.executeUpdate();

            // Create notes table
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
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement insertNote = connection.prepareStatement(
                        "INSERT INTO " + this.dbName + "." + username + bookName + " " +
                                "VALUES (?, ?);"
                )
        ) {
            for (Note note : notes)
            {
                insertNote.setString(1, note.getNote());
                insertNote.setInt(2, note.getPageNumber());

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
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement checkBookName = connection.prepareStatement(
                        "SELECT * FROM " + this.dbName + "." + username + " WHERE bookName = ?;"
                );
                PreparedStatement checkPage = connection.prepareStatement(
                        "SELECT * FROM " + this.dbName + "." + username + bookName + " WHERE page = ?;"
                );
                PreparedStatement insertNote = connection.prepareStatement(
                        "INSERT INTO " + this.dbName + "." + username + bookName + " " +
                                "VALUES (?, ?);"
                )
        ) {
            checkBookName.setString(1, bookName);

            ResultSet book = checkBookName.executeQuery();
            if (!book.next()) // Book's name doesn't exist
            {
                return false;
            }

            checkPage.setInt(1, note.getPageNumber());

            ResultSet set = checkPage.executeQuery();
            if (set.next()) // Page's number is used
            {
                return false;
            }

            insertNote.setString(1, note.getNote());
            insertNote.setInt(2, note.getPageNumber());

            insertNote.executeUpdate();
        }
        catch (SQLException e)
        {
            return false;
        }

        return true;
    }

    /**
     * Create a new label in user's database.
     *
     * @param username user's username.
     * @param label name of the label to add.
     * @return True if operation is performed successfully, false otherwise.
     */
    private boolean writeLabel(String username, String label)
    {
        // Add a new boolean column to user's books table.
        // Default value is false.
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement addColumn = connection.prepareStatement(
                        "ALTER TABLE " + this.dbName + "." + username + " ADD " + label + " bool NOT NULL DEFAULT 0;"
                );
        ) {
            addColumn.executeUpdate();
        }
        catch (SQLException e)
        {
            return false;
        }

        return true;
    }
}
