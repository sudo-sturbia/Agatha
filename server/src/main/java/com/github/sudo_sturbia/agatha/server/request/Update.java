package com.github.sudo_sturbia.agatha.server.request;

import com.github.sudo_sturbia.agatha.core.Book;
import com.github.sudo_sturbia.agatha.core.BookImp;
import com.github.sudo_sturbia.agatha.core.BookState;
import com.github.sudo_sturbia.agatha.core.BookStateDeserializer;
import com.github.sudo_sturbia.agatha.core.ExecutionState;
import com.github.sudo_sturbia.agatha.core.Note;
import com.github.sudo_sturbia.agatha.core.NoteDeserializer;
import com.github.sudo_sturbia.agatha.core.NoteImp;
import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Update handles client's UPDATE requests. Update is used to
 * update objects that already exist in Agatha's database.
 * <P>
 * An UPDATE request can be one of the following:
 * <P>
 * <pre>
 *     UPDATE username:password/b/bookName/{JSON}               // Replace book with given JSON object.
 *     UPDATE username:password/b/bookName/field=updated        // Update one of book's fields.
 *     UPDATE username:password/b/bookName/n/page/{JSON}        // Replace note at page.
 *     UPDATE username:password/b/bookName/n/page/field=updated // Update one of note's fields.
 *     UPDATE username:password/l/labelName/add/b/bookName      // Add label to book.
 *     UPDATE username:password/l/labelName/remove/b/bookName   // Remove label from book.
 * </pre>
 */
public class Update implements Request
{
    /** String representing request. */
    private final String request;

    /** Name of application's database. */
    private final String dbName;

    /**
     * Update's constructor. Used only by RequestBuilder.
     *
     * @param request request to handle.
     * @param dbName name of application's database.
     */
    Update(final String request, final String dbName)
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
        if (this.isUpdateBook())
        {
            return this.updateBook();
        }
        else if (this.isUpdateBooksField())
        {
            return this.updateBooksField();
        }
        else if (this.isUpdateNote())
        {
            return this.updateNote();
        }
        else if (this.isUpdateNotesField())
        {
            return this.updateNotesField();
        }
        else if (this.isAddLabelToBook())
        {
            return this.addLabelToBook();
        }
        else if (this.isRemoveLabelFromBook())
        {
            return this.removeLabelFromBook();
        }

        return new Gson().toJson(new ExecutionState(1)); // Wrong syntax
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     UPDATE username:password/b/bookName/{JSON}
     * </pre>
     *
     * @return True if update book request, false otherwise.
     */
    private boolean isUpdateBook()
    {
        return Pattern.compile("^UPDATE\\s+[^:]+:[^/:]+/b/[^/]+/\\{.+}$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Update a book in user's table.
     *
     * @return A JSON ExecutionState JSON object.
     */
    private String updateBook()
    {
        String[] list = RequestUtil.splitTwice(this.request, "/b/|/", "^UPDATE\\s+|:");

        String state;
        if ((state = RequestUtil.verify(this.dbName, list, 4)) != null)
        {
            return state;
        }

        Book book;
        Gson gson = new GsonBuilder().registerTypeAdapter(BookState.class, new BookStateDeserializer())
                                     .registerTypeAdapter(Note.class, new NoteDeserializer())
                                     .create();
        try {
            book = gson.fromJson(list[3], BookImp.class);
        }
        catch (JsonSyntaxException e) {
            return new Gson().toJson(new ExecutionState(3)); // JSON can't be unmarshalled
        }

        return !this.updateBook(book, list[0], list[2]) ?
                new Gson().toJson(new ExecutionState(3)) : // Operation failed
                new Gson().toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     UPDATE username:password/b/bookName/field=updated
     * </pre>
     *
     * @return True if update book's field request, false otherwise.
     */
    private boolean isUpdateBooksField()
    {
        return Pattern.compile("^UPDATE\\s+[^:]+:[^/:]+/b/[^/]+/[^/=]+=[^/=]+$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Update a field in a book in user's table.
     *
     * @return A JSON ExecutionState object.
     */
    private String updateBooksField()
    {
        String[] list = RequestUtil.removeEmpty(this.request.split("^UPDATE\\s+|:|/b/|/|="));

        String state;
        return (state = RequestUtil.verify(this.dbName, list, 5)) != null ?
                state :
                !this.updateBooksField(list[0], list[2], list[3], list[4]) ?
                        new Gson().toJson(new ExecutionState(3)) : // Operation failed
                        new Gson().toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     UPDATE username:password/b/bookName/n/page/{JSON}
     * </pre>
     *
     * @return True if update note request, false otherwise.
     */
    private boolean isUpdateNote()
    {
        return Pattern.compile("^UPDATE\\s+[^:]+:[^/:]+/b/[^/]+/n/[0-9]+/\\{.+}$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Update a note in user's book's table.
     *
     * @return A JSON ExecutionState object.
     */
    private String updateNote()
    {
        String[] list = RequestUtil.splitTwice(this.request, "/b/|/n/|/", "^UPDATE\\s+|:");


        String state;
        if ((state = RequestUtil.verify(this.dbName, list, 5)) != null)
        {
            return state;
        }

        Note note;
        Gson gson = new Gson();
        try {
            note = gson.fromJson(list[4], NoteImp.class);
        }
        catch (JsonSyntaxException e) {
            return gson.toJson(new ExecutionState(3)); // JSON can't be unmarshalled
        }

        return !this.updateNote(note, list[0], list[2], Integer.parseInt(list[3])) ?
                gson.toJson(new ExecutionState(3)) : // Operation failed
                gson.toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     UPDATE username:password/b/bookName/n/page/field=updated
     * </pre>
     *
     * @return True if update note's field request, false otherwise.
     */
    private boolean isUpdateNotesField()
    {
        return Pattern.compile("^UPDATE\\s+[^:]+:[^/:]+/b/[^/]+/n/[0-9]+/[^/=]+=[^/=]+$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Update a field in a note in a book belonging to the user.
     *
     * @return A JSON ExecutionState object.
     */
    private String updateNotesField()
    {
        String[] list = RequestUtil.removeEmpty(this.request.split("^UPDATE\\s+|:|/b/|/n/|/|="));

        String state;
        return (state = RequestUtil.verify(this.dbName, list, 6)) != null ?
                state :
                !this.updateNotesField(list[0], list[2], Integer.parseInt(list[3]), list[4], list[5]) ?
                        new Gson().toJson(new ExecutionState(3)) : // Operation failed
                        new Gson().toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     UPDATE username:password/l/labelName/add/b/bookName
     * </pre>
     *
     * @return True if add label to book request, false otherwise.
     */
    private boolean isAddLabelToBook()
    {
        return Pattern.compile("^UPDATE\\s+[^:]+:[^/:]+/l/[^/]+/add/b/[^/]+$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Add a label to a book. If label doesn't exist create a new
     * one.
     *
     * @return A JSON ExecutionState object.
     */
    private String addLabelToBook()
    {
        String[] list = RequestUtil.removeEmpty(this.request.split("^UPDATE\\s+|:|/l/|/add/b/"));

        String state;
        return (state = RequestUtil.verify(this.dbName, list, 4)) != null ?
                state :
                !this.bookLabel(list[0], list[3], list[2], true) ?
                        new Gson().toJson(new ExecutionState(3)) : // Operation failed
                        new Gson().toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Returns true if request is of the form
     * <pre>
     *     UPDATE username:password/l/labelName/remove/b/bookName
     * </pre>
     *
     * @return True if remove label from book request, false otherwise.
     */
    private boolean isRemoveLabelFromBook()
    {
        return Pattern.compile("^UPDATE\\s+[^:]+:[^/:]+/l/[^/]+/remove/b/[^/]+\\s*$", Pattern.CASE_INSENSITIVE)
                .matcher(this.request).matches();
    }

    /**
     * Remove a label from a book.
     *
     * @return A JSON ExecutionState object.
     */
    private String removeLabelFromBook()
    {
        String[] list = RequestUtil.removeEmpty(this.request.split("^UPDATE\\s+|:|/l/|/remove/b/"));

        String state;
        return (state = RequestUtil.verify(this.dbName, list, 4)) != null ?
                state :
                !this.bookLabel(list[0], list[3], list[2], false) ?
                        new Gson().toJson(new ExecutionState(3)) : // Operation failed
                        new Gson().toJson(new ExecutionState(0));  // Successful
    }

    /**
     * Update an existing book with information from given
     * book.
     *
     * @param book new book to write to database.
     * @param username user's username.
     * @param bookName name of book to update.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean updateBook(Book book, String username, String bookName)
    {
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement updateBook = connection.prepareStatement(
                        "UPDATE " + this.dbName + "." + Sanitizer.sanitize(username) + " " +
                                "SET " +
                                "bookName = ?, " +
                                "author = ?, " +
                                "state = ?, " +
                                "pages = ?, " +
                                "readPages = ?, " +
                                "coverPath = ?, " +
                                "hasNotes = ? " +
                                "WHERE bookName = ?;"
                );
        ) {
            updateBook.setString(1, book.getName());
            updateBook.setString(2, book.getAuthor());
            updateBook.setString(3, book.getStateToString());
            updateBook.setInt(4, book.getNumberOfPages());
            updateBook.setInt(5, book.getNumberOfReadPages());
            updateBook.setString(6, book.getCoverImagePath());
            updateBook.setBoolean(7, book.getNotes().size() > 0);

            updateBook.setString(8, bookName);

            updateBook.executeUpdate();

            if (this.updateNotes(book.getNotes(), username, bookName))
            {
                return true;
            }
        }
        catch (SQLException e)
        {
            // Operation failed
        }

        return false;
    }

    /**
     * Delete all notes in a book and replace them with the
     * given list.
     *
     * @param notes notes to write.
     * @param username user's username.
     * @param bookName name of book to update.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean updateNotes(List<Note> notes, String username, String bookName)
    {
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement deleteOld = connection.prepareStatement(
                        "DELETE FROM " + this.dbName + "." + Sanitizer.sanitize(username + bookName) + ";"
                );
                PreparedStatement insertNotes = connection.prepareStatement(
                        "INSERT INTO " + this.dbName + "." + Sanitizer.sanitize(username + bookName) + " " +
                                "VALUES (?, ?);"
                );
        ) {
            deleteOld.executeUpdate();

            for (Note note : notes)
            {
                insertNotes.setString(1, note.getNote());
                insertNotes.setInt(2, note.getPageNumber());

                insertNotes.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            return false;
        }

        return true;
    }

    /**
     * Verifies a given field name and updates its value if
     * the field exists.
     *
     * @param username user's username.
     * @param bookName name of book to update.
     * @param fieldName name of field to update.
     * @param fieldValue value to update field with.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean updateBooksField(String username, String bookName, String fieldName, String fieldValue)
    {
        // Verify field's name, and Add '' around fieldValue
        // if field is a varchar.
        if (Arrays.asList("author", "state", "coverPath").contains(fieldName))
        {
            fieldValue = "'" + fieldValue + "'";
        }
        else if (!fieldName.equals("readPages"))
        {
            return false;
        }

        // Update value in database
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement updateBook = connection.prepareStatement(
                        "UPDATE " + this.dbName + "." + Sanitizer.sanitize(username) + " " +
                                "SET " +
                                fieldName + " = " + fieldValue + " " +
                                "WHERE bookName = ?;"
                );
        ) {
            updateBook.setString(1, bookName);
            updateBook.executeUpdate();
        }
        catch (SQLException e)
        {
            // Operation failed
            return false;
        }

        return true;
    }

    /**
     * Update a note in user's book's database using values from
     * a given object.
     *
     * @param note updated note object.
     * @param username user's username.
     * @param bookName name of book containing the note.
     * @param page number of page containing the note.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean updateNote(Note note, String username, String bookName, int page)
    {
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement updateNote = connection.prepareStatement(
                        "UPDATE " + this.dbName + "." + Sanitizer.sanitize(username + bookName) + " " +
                                "SET " +
                                "note = ?," +
                                "page = ? " +
                                "WHERE page = ?;"
                );
        ) {
            updateNote.setString(1, note.getNote());
            updateNote.setInt(2, page);
            updateNote.setInt(3, page);
            updateNote.executeUpdate();
        }
        catch (SQLException e)
        {
            // Operation failed
            return false;
        }

        return true;
    }

    /**
     * Verifies a given field name and updates its value if
     * the field exists.
     *
     * @param username user's username.
     * @param bookName name of book containing the note.
     * @param page number of page containing the note.
     * @param fieldName name of the field to update.
     * @param fieldValue value to update the field with.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean updateNotesField(String username, String bookName, int page, String fieldName, String fieldValue)
    {
        // Verify field's name, and Add '' to fieldValue if
        // field is a varchar.
        if (fieldName.equals("note"))
        {
            fieldValue = "'" + fieldValue + "'";
        }
        else if (!fieldName.equals("page"))
        {
            return false;
        }

        // Update value in database
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement updateNote = connection.prepareStatement(
                        "UPDATE " + this.dbName + "." + Sanitizer.sanitize(username + bookName) + " " +
                                "SET " +
                                fieldName + " = " + fieldValue + " " +
                                "WHERE page = ?;"
                );
        ) {
            updateNote.setInt(1, page);
            updateNote.executeUpdate();
        }
        catch (SQLException e)
        {
            // Operation failed
            return false;
        }

        return true;
    }

    /**
     * Add or remove a label from a book.
     *
     * @param username user's username.
     * @param bookName name of the book to add the label to.
     * @param label label to add to the book.
     * @param isAdd true if label is to be added, false otherwise.
     * @return True if operation succeeded, false otherwise.
     */
    private boolean bookLabel(String username, String bookName, String label, boolean isAdd)
    {
        try (
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement addLabel = connection.prepareStatement(
                        "UPDATE " + this.dbName + "." + Sanitizer.sanitize(username) + " " +
                                "SET " +
                                label + " = ? " +
                                "WHERE bookName = ?;"
                );
        ) {
            addLabel.setBoolean(1, isAdd);
            addLabel.setString(2, bookName);
            addLabel.executeUpdate();
        }
        catch (SQLException e)
        {
            // Operation failed
            return false;
        }

        return true;
    }
}
