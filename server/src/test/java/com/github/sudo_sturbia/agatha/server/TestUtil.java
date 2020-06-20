package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.core.Book;
import com.github.sudo_sturbia.agatha.core.BookBuilder;
import com.github.sudo_sturbia.agatha.core.Note;
import com.github.sudo_sturbia.agatha.core.NoteImp;
import com.github.sudo_sturbia.agatha.server.clients.ClientManager;
import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods used in server tests.
 */
public class TestUtil
{
    private static final String dbName = "testDB";

    /**
     * Setup initial database for testing.
     */
    public static void setup()
    {
        // !!
        // Insert username and password used for MySQL below
        // before running tests.
        // !!
        try
        {
            ServerBuilder.newServer()
                         .dbName(TestUtil.dbName)
                         .connector(ConnectorBuilder.ConnectorType.NORMAL)
                      // .dbServerUsername("")
                      // .dbServerPass("")
                         .build();
        }
        catch (IllegalStateException e)
        {
            // To avoid errors when setup is called more than once
        }
        finally
        {
            ServerSetupManager.setup(TestUtil.dbName, false);
        }
    }

    /**
     * Clean resources after test is finished (delete testing database.)
     */
    public static void clean()
    {
        // Drop testDB
        try (Connection connection = ConnectorBuilder.connector().connection();
             PreparedStatement drop = connection.prepareStatement("DROP DATABASE " + TestUtil.dbName + ";"))
        {
            drop.executeUpdate();
        }
        catch (SQLException e)
        {
            System.err.println("Failed to clean after testing.");
        }

        ConnectorBuilder.connector().clean();
        ClientManager.get().stopManagerThread();
    }

    /**
     * Create a user, book, note, and label to use for testing.
     */
    public static void create()
    {
        final Gson gson = new Gson();

        Book book = BookBuilder.newBook("My Book", 100).build();
        Note note = new NoteImp(100, "Note #1", 10);
        book.addNote(note);

        List<String> requests = new ArrayList<>();
        requests.add("CREATE username:password");
        requests.add("CREATE username:password/b/" + gson.toJson(book));
        requests.add("CREATE username:password/l/label");

        for (String request : requests)
        {
            Protocol.handle(request, TestUtil.dbName);
        }
    }

    /**
     * Create a user, book, note, and label with the given name
     * to use for testing.
     */
    public static void create(String username)
    {
        final Gson gson = new Gson();

        Book book = BookBuilder.newBook("My Book", 100).build();
        Note note = new NoteImp(100, "Note #1", 10);
        book.addNote(note);

        List<String> requests = new ArrayList<>();
        requests.add("CREATE " + username + ":password");
        requests.add("CREATE " + username + ":password/b/" + gson.toJson(book));
        requests.add("CREATE " + username + ":password/l/label");

        for (String request : requests)
        {
            Protocol.handle(request, TestUtil.dbName);
        }
    }
}
