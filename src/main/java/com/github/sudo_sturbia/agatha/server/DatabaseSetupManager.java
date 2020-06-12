package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DatabaseSetupManager creates a new database to be used by the
 * server and performs initial configuration on it.
 * <p>
 * An Agatha database consists of a Users table, a table for each
 * client containing client's books, and a table for each book containing
 * book's notes. DatabaseSetupManager sets up the initial database
 * with the Users table, the rest are left to be created on demand.
 */
public class DatabaseSetupManager
{
    /**
     * Setup a Connector and Create a new database containing Users' table.
     *
     * @param dbName name of the database to create.
     * @throws ServerSetupException if setup can't be performed.
     */
    public static void setup(String dbName) throws ServerSetupException
    {
        try {
            ConnectorBuilder.get().setup();
        }
        catch (SQLException e) {
            throw new ServerSetupException("Couldn't setup database connector.");
        }

        try (
                // Create database and users table
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement database = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS ?;");
                PreparedStatement usersTable = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS ?.Users (" +
                                "username varchar(255) NOT NULL, " +
                                "password char(64) NOT NULL, " +
                                "salt char(16) NOT NULL, " +
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
