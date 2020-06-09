package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DatabaseSetupManager performs initial configuration for a new database.
 */
public class DatabaseSetupManager
{
    /**
     * Create a new database containing one user table.
     *
     * @param dbName name of the database to create.
     * @throws ServerSetupException if setup can't be performed.
     */
    public static void setup(String dbName) throws ServerSetupException
    {
        try (
                // Create database and users table
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement database = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS ?;");
                PreparedStatement usersTable = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS ?.Users (" +
                                "username varchar(255) NOT NULL, " +
                                "password varchar(64) NOT NULL, " +
                                "PRIMARY KEY(username)" +
                                ");")
        ) {
            database.setString(1, dbName);
            database.executeUpdate();

            usersTable.setString(1, dbName);
            usersTable.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new ServerSetupException("Couldn't perform initial database setup.");
        }
    }
}
