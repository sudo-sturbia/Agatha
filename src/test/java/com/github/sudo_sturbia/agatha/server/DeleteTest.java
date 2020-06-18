package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.server.request.ExecutionState;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test handling of DELETE requests.
 */
public class DeleteTest
{
    @BeforeAll
    static void setup()
    {
        TestUtil.setup();
    }

    @DisplayName("Test DELETE client.")
    @Test
    void deleteClient()
    {
        String dbName = "testDB";
        Protocol.handle("CREATE username:password", dbName);

        String response = Protocol.handle("DELETE username:password", dbName);
        assertEquals(0, new Gson().fromJson(response, ExecutionState.class).getCode(),
                "DELETE client failed.");
    }

    @DisplayName("Test DELETE book.")
    @Test
    void deleteBook()
    {
        String dbName = "testDB";
        TestUtil.create("user1");

        String response = Protocol.handle("DELETE user1:password/b/My Book", dbName);
        assertEquals(0, new Gson().fromJson(response, ExecutionState.class).getCode(),
                "DELETE book failed.");
    }

    @DisplayName("Test DELETE all books.")
    @Test
    void deleteAllBooks()
    {
        String dbName = "testDB";
        TestUtil.create("user2");

        String response = Protocol.handle("DELETE user2:password/b/*", dbName);
        assertEquals(0, new Gson().fromJson(response, ExecutionState.class).getCode(),
                "DELETE all books failed.");
    }

    @DisplayName("Test DELETE label.")
    @Test
    void deleteLabel()
    {
        String dbName = "testDB";
        TestUtil.create("user3");

        String response = Protocol.handle("DELETE user3:password/l/label", dbName);
        assertEquals(0, new Gson().fromJson(response, ExecutionState.class).getCode(),
                "DELETE label failed.");
    }

    @DisplayName("Test DELETE all labels.")
    @Test
    void deleteAllLabels()
    {
        String dbName = "testDB";
        TestUtil.create("user4");

        String response = Protocol.handle("DELETE user4:password/l/*", dbName);
        assertEquals(0, new Gson().fromJson(response, ExecutionState.class).getCode(),
                "DELETE all labels failed.");
    }

    @DisplayName("Test DELETE note.")
    @Test
    void deleteNote()
    {
        String dbName = "testDB";
        TestUtil.create("user5");

        String response = Protocol.handle("DELETE user5:password/b/My Book/n/10", dbName);
        assertEquals(0, new Gson().fromJson(response, ExecutionState.class).getCode(),
                "DELETE note failed.");
    }

    @DisplayName("Test DELETE all notes.")
    @Test
    void deleteAllNotes()
    {
        String dbName = "testDB";
        TestUtil.create("user6");

        String response = Protocol.handle("DELETE user6:password/b/My Book/n/*", dbName);
        assertEquals(0, new Gson().fromJson(response, ExecutionState.class).getCode(),
                "DELETE all notes failed.");
    }

    @AfterAll
    static void clean()
    {
        TestUtil.clean();
    }
}
