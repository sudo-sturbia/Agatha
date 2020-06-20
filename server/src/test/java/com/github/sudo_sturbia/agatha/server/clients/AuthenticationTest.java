package com.github.sudo_sturbia.agatha.server.clients;

import com.github.sudo_sturbia.agatha.server.Protocol;
import com.github.sudo_sturbia.agatha.server.TestUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test verification of clients using the client verification system.
 */
public class AuthenticationTest
{
    @BeforeAll
    static void setup()
    {
        TestUtil.setup();

        String dbName = "testDB";
        for (int i = 0; i < 50; i++)
        {
            Protocol.handle("CREATE username" + i + ":password" + i, dbName);
        }
    }

    @DisplayName("Test verification of correct passwords.")
    @Test
    void correct()
    {
        String dbName = "testDB";
        for (int i = 0; i < 50; i++)
        {
            assertEquals(ClientManager.LoginStatus.SUCCEEDED, ClientManager.get().doesExist(dbName, "username" + i, "password" + i),
                    "Authentication failed.");
        }
    }

    @DisplayName("Test authentication using incorrect passwords.")
    @Test
    void incorrect()
    {
        String dbName = "testDB";
        for (int i = 0; i < 50; i++)
        {
            assertEquals(ClientManager.LoginStatus.FAILED, ClientManager.get().doesExist(dbName, "username" + i, "password" + (i + 1)),
                    "Incorrect authentication succeeded.");
        }
    }

    @DisplayName("Test account lockout.")
    @Test
    void timeout()
    {
        String dbName = "testDB";

        // Create five new accounts
        for (int i = 0; i < 5; i++)
        {
            Protocol.handle("CREATE timeout" + i + ":password" + i, dbName);
        }

        // Fail to login five times for each account
        for (int i = 0; i < 5; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                assertEquals(ClientManager.LoginStatus.FAILED, ClientManager.get().doesExist(dbName, "timout" + i, "password"),
                        "Incorrect authentication succeeded.");
            }
        }

        // Test account locking with wrong passwords
        for (int i = 0; i < 5; i++)
        {
            assertEquals(ClientManager.LoginStatus.TIMEOUT, ClientManager.get().doesExist(dbName, "timout" + i, "password"),
                    "Account locking fails.");
        }

        // Test account locking with correct passwords
        for (int i = 0; i < 5; i++)
        {
            assertEquals(ClientManager.LoginStatus.TIMEOUT, ClientManager.get().doesExist(dbName, "timout" + i, "password" + i),
                    "Account locking fails.");
        }
    }

    @AfterAll
    static void clean()
    {
        TestUtil.clean();
    }
}
