package com.github.sudo_sturbia.agatha.server.request;

import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Delete handles client's DELETE requests. Delete is used to
 * remove data that exists in the application's database.
 * <P>
 * A DELETE request can be one of the following:
 * <P>
 * <pre>
 *     DELETE username:password                   // Delete client from database.
 *     DELETE username:password/b/bookName        // Delete book.
 *     DELETE username:password/b/*               // Delete all books.
 *     DELETE username:password/l/labelName       // Delete label.
 *     DELETE username:password/l/*               // Delete all labels.
 *     DELETE username:password/b/bookName/n/page // Delete note at page.
 *     DELETE username:password/b/bookName/n/*    // Delete all book's notes.
 * </pre>
 */
public class Delete implements Request
{
    /** String representing request. */
    private final String request;

    /** Name of application's database. */
    private final String dbName;

    /**
     * Delete's constructor. Used only by RequestBuilder.
     *
     * @param request request to handle.
     * @param dbName name of application's database.
     */
    Delete(final String request, final String dbName)
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
        if (this.isDeleteClient())
        {
            return this.deleteClient();
        }
        else if (this.isDeleteBook())
        {
            return this.deleteBook();
        }
        else if (this.isDeleteAllBooks())
        {
            return this.deleteAllBooks();
        }
        else if (this.isDeleteLabel())
        {
            return this.deleteLabel();
        }
        else if (this.isDeleteAllLabels())
        {
            return this.deleteAllLabels();
        }
        else if (this.isDeleteNote())
        {
            return this.deleteNote();
        }
        else if (this.isDeleteAllNotes())
        {
            return this.deleteAllNotes();
        }

        return new Gson().toJson(new ExecutionState(1)); // Wrong syntax
    }

    /**
     * Check if given request string is correct.
     *
     * @return True if request is correct, false otherwise.
     */
    private boolean isCorrect()
    {
        return Pattern.compile("^DELETE\\s+[^:]+:[^:/]+(/(b/[^/]+(/n/([0-9]+|\\*)|)|l/[^/]+)|)\\s*$",
                Pattern.CASE_INSENSITIVE).matcher(request).matches();
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     DELETE username:password
     * </pre>
     *
     * @return True if delete user request, false otherwise.
     */
    private boolean isDeleteClient()
    {
        return Pattern.compile("^DELETE\\s+[^:]+:[^:/]+$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Delete a client from application's database.
     *
     * @return A JSON ExecutionState object.
     */
    private String deleteClient()
    {
        String[] list = this.request.split("^DELETE\\s+|:");

        String state;
        return (state = RequestUtil.verify(this.dbName, list, 2)) != null ?
                state :
                !this.deleteClient(list[0]) ?
                        new Gson().toJson(new ExecutionState(3)) : // Operation failed
                        new Gson().toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     DELETE username:password/b/bookName
     * </pre>
     *
     * @return True if delete book request, false otherwise.
     */
    private boolean isDeleteBook()
    {
        return Pattern.compile("^DELETE\\s+[^:]+:[^:/]+/b/[^/]+$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Delete a book from user's table.
     *
     * @return A JSON ExecutionState object.
     */
    private String deleteBook()
    {
        String[] list = this.request.split("^DELETE\\s+|:|/b/");

        String state;
        return (state = RequestUtil.verify(this.dbName, list, 3)) != null ?
                state :
                !this.deleteBook(list[0], list[2]) ?
                        new Gson().toJson(new ExecutionState(3)) : // Operation failed
                        new Gson().toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     DELETE username:password/b/*
     * </pre>
     *
     * @return True if delete all books request, false otherwise.
     */
    private boolean isDeleteAllBooks()
    {
        return Pattern.compile("^DELETE\\s+[^:]+:[^:/]+/b/\\*$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Delete all books that exist in user's table.
     *
     * @return A JSON ExecutionState object.
     */
    private String deleteAllBooks()
    {
        String[] list = this.request.split("^DELETE\\s+|:|/b/\\*$");

        String state;
        return (state = RequestUtil.verify(this.dbName, list, 2)) != null ?
                state :
                !this.deleteAllBooks(list[0]) ?
                        new Gson().toJson(new ExecutionState(3)) : // Operation failed
                        new Gson().toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     DELETE username:password/l/labelName
     * </pre>
     *
     * @return True if delete label request, false otherwise.
     */
    private boolean isDeleteLabel()
    {
        return Pattern.compile("^DELETE\\s+[^:]+:[^:/]+/l/[^/]+$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Delete a label from user's table.
     *
     * @return A JSON ExecutionState object.
     */
    private String deleteLabel()
    {
        String[] list = this.request.split("^DELETE\\s+|:|/l/");

        String state;
        return (state = RequestUtil.verify(this.dbName, list, 3)) != null ?
                state :
                !this.deleteLabel(list[0], list[2]) ?
                        new Gson().toJson(new ExecutionState(3)) : // Successful
                        new Gson().toJson(new ExecutionState(0));  // Operation failed
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     DELETE username:password/l/*
     * </pre>
     *
     * @return True if delete all labels request, false otherwise.
     */
    private boolean isDeleteAllLabels()
    {
        return Pattern.compile("^DELETE\\s+[^:]+:[^:/]+/l/\\*$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Delete all labels in user's table.
     *
     * @return A JSON ExecutionState object.
     */
    private String deleteAllLabels()
    {
        String[] list = this.request.split("^DELETE\\s+|:|/l/\\*$");

        String state;
        return (state = RequestUtil.verify(this.dbName, list, 2)) != null ?
                state :
                !this.deleteAllLabels(list[0]) ?
                        new Gson().toJson(new ExecutionState(3)) : // Operation failed
                        new Gson().toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     DELETE username:password/b/bookName/n/page
     * </pre>
     *
     * @return True if delete note request, false otherwise.
     */
    private boolean isDeleteNote()
    {
        return Pattern.compile("^DELETE\\s+[^:]+:[^:/]+/b/[^/]+/n/[0-9]+$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Delete a note from user's book.
     *
     * @return A JSON ExecutionState object.
     */
    private String deleteNote()
    {
        String[] list = this.request.split("^DELETE\\s+|:|/b/|/n/");

        String state;
        return (state = RequestUtil.verify(this.dbName, list, 4)) != null ?
                state :
                !this.deleteNote(list[0], list[2], Integer.parseInt(list[3])) ?
                        new Gson().toJson(new ExecutionState(3)) : // Operation failed
                        new Gson().toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     DELETE username:password/b/bookName/n/*
     * </pre>
     *
     * @return True if delete all notes request, false otherwise.
     */
    private boolean isDeleteAllNotes()
    {
        return Pattern.compile("^DELETE\\s+[^:]+:[^:/]+/b/[^/]+/n/\\*$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Delete all notes in user's book.
     *
     * @return A JSON ExecutionState object.
     */
    private String deleteAllNotes()
    {
        String[] list = this.request.split("^DELETE\\s+|:|/b/|/n/\\*$");

        String state;
        return (state = RequestUtil.verify(this.dbName, list, 3)) != null ?
                state :
                !this.deleteAllNotes(list[0], list[2]) ?
                        new Gson().toJson(new ExecutionState(3)) : // Operation failed
                        new Gson().toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Delete all client associated data from database.
     *
     * @param username user's username.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean deleteClient(String username)
    {
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement getNames = connection.prepareStatement(
                        "SELECT bookName FROM ?.?"
                );
                PreparedStatement dropTable = connection.prepareStatement(
                        "DROP TABLE ?.?"
                )
        ) {
            dropTable.setString(1, this.dbName);

            getNames.setString(1, this.dbName);
            getNames.setString(2, username);

            // Delete book's tables
            ResultSet set = getNames.executeQuery();
            while (set.next())
            {
                dropTable.setString(2, username + set.getString("bookName"));
                dropTable.executeUpdate();
            }

            // Delete user table
            dropTable.setString(2, username);
            dropTable.executeUpdate();
        }
        catch (SQLException e)
        {
            // Operation failed
            return false;
        }

        return true;
    }

    /**
     * Delete a book from user's table.
     *
     * @param username user's username.
     * @param bookName name of book to delete.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean deleteBook(String username, String bookName)
    {
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement deleteBook = connection.prepareStatement(
                        "DELETE FROM ?.? WHERE bookName = '?'"
                );
                PreparedStatement dropTable = connection.prepareStatement(
                        "DROP TABLE ?.?"
                )
        ) {
            deleteBook.setString(1, this.dbName);
            deleteBook.setString(2, username);
            deleteBook.setString(3, bookName);

            deleteBook.executeUpdate();

            dropTable.setString(1, this.dbName);
            dropTable.setString(2, username + bookName);

            dropTable.executeUpdate();
        }
        catch (SQLException e)
        {
            // Operation failed
            return false;
        }

        return true;
    }

    /**
     * Delete all books from user's table.
     *
     * @param username user's username.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean deleteAllBooks(String username)
    {
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement deleteBooks = connection.prepareStatement(
                        "DELETE FROM ?.?"
                );
        ) {
            deleteBooks.setString(1, this.dbName);
            deleteBooks.setString(2, username);

            deleteBooks.executeUpdate();
        }
        catch (SQLException e)
        {
            // Operation failed
            return false;
        }

        return true;
    }

    /**
     * Delete a label from user's table.
     *
     * @param username user's username.
     * @param label name of the label to be deleted.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean deleteLabel(String username, String label)
    {
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement deleteLabel = connection.prepareStatement(
                        "ALTER TABLE ?.? DROP COLUMN ?"
                );
        ) {
            deleteLabel.setString(1, this.dbName);
            deleteLabel.setString(2, username);
            deleteLabel.setString(3, label);

            deleteLabel.executeUpdate();
        }
        catch (SQLException e)
        {
            // Operation failed
            return false;
        }

        return true;
    }

    /**
     * Delete all labels from user's database.
     *
     * @param username user's username.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean deleteAllLabels(String username)
    {
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement getLabels = connection.prepareStatement(
                        "SELECT * FROM ?.?"
                );
                PreparedStatement deleteLabel = connection.prepareStatement(
                        "ALTER TABLE ?.? DROP COLUMN ?"
                );
        ) {
            deleteLabel.setString(1, this.dbName);
            deleteLabel.setString(2, username);

            getLabels.setString(1, this.dbName);
            getLabels.setString(2, username);

            ResultSet set = getLabels.executeQuery();
            ResultSetMetaData data = set.getMetaData();

            // Find label names and delete them
            List<String> columns = new ArrayList<>(Arrays.asList("bookName", "author", "state", "pages", "readPages", "coverPath"));
            for (int i = 1, count = data.getColumnCount(); i <= count; i++)
            {
                // If column is a label
                String columnName = data.getColumnName(i);
                if (!columns.contains(columnName))
                {
                    deleteLabel.setString(3, columnName);
                    deleteLabel.executeUpdate();
                }
            }
        }
        catch (SQLException e)
        {
            // Operation failed
            return false;
        }

        return true;
    }

    /**
     * Delete a note from user's book.
     *
     * @param username user's username.
     * @param bookName name of the book containing the note.
     * @param page number of the page containing the note.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean deleteNote(String username, String bookName, int page)
    {
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement deleteNote = connection.prepareStatement(
                        "DELETE FROM ?.? " +
                                "WHERE page = ?"
                );
        ) {
            deleteNote.setString(1, this.dbName);
            deleteNote.setString(2, username + bookName);
            deleteNote.setInt(3, page);

            deleteNote.executeUpdate();
        }
        catch (SQLException e)
        {
            // Operation failed
            return false;
        }

        return true;
    }

    /**
     * Delete all notes from user's book.
     *
     * @param username user's username.
     * @param bookName name of the book containing the note.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean deleteAllNotes(String username, String bookName)
    {
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement deleteNotes = connection.prepareStatement(
                        "DELETE FROM ?.?"
                );
        ) {
            deleteNotes.setString(1, this.dbName);
            deleteNotes.setString(2, username + bookName);

            deleteNotes.executeUpdate();
        }
        catch (SQLException e)
        {
            // Operation failed
            return false;
        }

        return true;
    }
}
