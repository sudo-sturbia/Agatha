package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.server.clients.ClientManager;
import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * ServerSetupManager creates a new database to be used by the
 * server, performs initial configuration on it, and creates a shutdown
 * hook to terminate all components when the server finishes execution.
 * <p>
 * An Agatha database consists of a Users table, a table for each
 * client containing client's books, and a table for each book containing
 * book's notes. DatabaseSetupManager sets up the initial database
 * with the Users table, the rest are left to be created on demand.
 */
public class ServerSetupManager
{
    /**
     * Setup a Connector, Create a new application database containing
     * Users' table, and create a shutdown hook.
     *
     * @param dbName name of the database to create.
     * @param addHook shutdown hook is only added if this parameter is true.
     * @throws ServerSetupException if setup can't be performed.
     */
    public static void setup(String dbName, boolean addHook) throws ServerSetupException
    {
        try {
            ConnectorBuilder.connector().setup();
        }
        catch (SQLException e) {
            throw new ServerSetupException("Couldn't setup database connector.");
        }

        if (addHook)
        {
            ServerSetupManager.addShutdownHook();;
        }

        try (
                // Create database and users table
                Connection connection = ConnectorBuilder.connector().connection();
                PreparedStatement database = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + dbName + ";");
                PreparedStatement usersTable = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS " + dbName + ".Users (" +
                                "username varchar(255) NOT NULL, " +
                                "password char(64) NOT NULL, " +
                                "salt char(16) NOT NULL, " +
                                "PRIMARY KEY(username)" +
                                ");")
        ) {
            database.executeUpdate();
            usersTable.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new ServerSetupException("Couldn't perform initial database setup.");
        }
    }

    /**
     * Create a shutdown hook to be run when the server finished execution.
     */
    private static void addShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            ConnectorBuilder.connector().clean();
            ClientManager.get().stopManagerThread();
        }));
    }
}
