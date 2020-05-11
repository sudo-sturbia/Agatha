package com.github.sudo_sturbia.agatha.server.request;

import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserManager handles operations related to users table, such as
 * username/password verification, and username existence.
 */
public class UserManager
{
    /**
     * Verify that username exists in the database.
     *
     * @param dbName name of application's database.
     * @param username username to verify.
     * @return True if username exists, false otherwise.
     */
    public static boolean doesExist(String dbName, String username)
    {
        boolean exists = true; // Assume that username exists for safety
        try (
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM ?.Users WHERE username=?;");
        ) {
            statement.setString(1, dbName);
            statement.setString(2, username);

            try (ResultSet set = statement.executeQuery())
            {
                // If username doesn't exist
                if (!set.next())
                {
                    exists = false;
                }
            }
        }
        catch (SQLException e)
        {
            // Return ...
        }

        return exists;
    }

    /**
     * Verify client's credentials (username, and password.)
     *
     * @param dbName name of application's database.
     * @param username username to verify.
     * @param password password to verify.
     * @return True if given credentials exist.
     */
    public static boolean doesExist(String dbName, String username, String password)
    {
        // Encrypt password using sha256
        String encrypted = DigestUtils.sha256Hex(password);

        boolean exists = false; // Assume that credentials are wrong for safety
        try (
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM ?.Users WHERE username=?;");
        ) {
            statement.setString(1, dbName);
            statement.setString(2, username);

            try (ResultSet set = statement.executeQuery())
            {
                // Verify credentials
                if (set.next())
                {
                    if (set.getString("username").equals(username) &&
                        set.getString("password").equals(encrypted))
                    {
                        exists = true;
                    }
                }
            }
        }
        catch (SQLException e)
        {
            // Return ...
        }

        return exists;
    }
}
