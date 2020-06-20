package com.github.sudo_sturbia.agatha.server.clients;

import com.github.sudo_sturbia.agatha.server.Protocol;
import com.github.sudo_sturbia.agatha.server.TestUtil;
import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * SaltTest tests that similar passwords are stored differently
 * in application's database (hashed differently.)
 */
public class SaltTest
{
    @BeforeAll
    static void setup()
    {
        TestUtil.setup();
    }

    @DisplayName("Test salting of passwords.")
    @Test
    void salt()
    {
        String dbName = "testDB";
        for (int i = 0; i < 50; i++)
        {
            Protocol.handle("CREATE username" + i + ":password", dbName);
        }

        List<String> passwords = this.passwords();
        assertEquals(50, passwords.size(), "Wrong number of passwords.");

        for (int i = 0; i < 50; i++)
        {
            for (int j = i + 1; j < 50; j++)
            {
                assertNotEquals(passwords.get(j), passwords.get(i), "Hashes are similar.");
            }
        }
    }

    /** Get a list of all hashed passwords in the database. */
    List<String> passwords()
    {
        String dbName = "testDB";
        List<String> passwords = new ArrayList<>();

        try (Connection connection = ConnectorBuilder.connector().connection();
             PreparedStatement select = connection.prepareStatement(
                     "SELECT password FROM " + dbName + ".Users"))
        {
            ResultSet set = select.executeQuery();
            while (set.next())
            {
                passwords.add(set.getString("password"));
            }
        }
        catch (SQLException e)
        {
            // Do nothing.
        }

        return passwords;
    }

    @AfterAll
    static void clean()
    {
        TestUtil.clean();
    }
}
