package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.server.database.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * ServerImp Implementation of Agatha's server. Server uses
 * a custom communication protocol that supports the four main
 * CRUD functions.
 * @see com.github.sudo_sturbia.agatha.server.Protocol
 */
public class ServerImp implements Server
{
    /** Name of Application's database. */
    private final String dbName;

    /** Port to listen to. */
    private final int port;

    /** Database connector. */
    private final Connector connector;

    /**
     * ServerImp's constructor.
     *
     * @param dbName name of application's database.
     * @param port port to listen to.
     * @param connector database connector.
     */
    ServerImp(String dbName, int port, Connector connector)
    {
        this.dbName = dbName;
        this.port = port;
        this.connector = connector;
    }


    @Override
    public void run() throws ServerSetupException
    {
        Connection connection;
        try
        {
            connector.setup();
            connection = connector.get();
        }
        catch (SQLException e)
        {
            throw new ServerSetupException("Couldn't establish a database connection.");
        }

        this.setup(connection);

        // TODO
        //      Listen to port and handle requests ..

        try
        {
            connection.close();
        }
        catch (SQLException e)
        {
            throw new ServerSetupException("Couldn't close connection.");
        }
    }

    /**
     * Perform initial database setup.
     *
     * @param connection connection to application's database.
     * @throws ServerSetupException if setup statements can't be executed.
     */
    private void setup(Connection connection) throws ServerSetupException
    {
        try (
            // Create database and users table
            PreparedStatement database = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS ?");
            PreparedStatement usersTable = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS ?.Users " +
                            "(username varchar(255) NOT NULL, password varchar(64) NOT NULL, PRIMARY KEY(username))")
        ) {
            database.setString(1, this.dbName);
            usersTable.setString(1, this.dbName);

            database.executeUpdate();
            usersTable.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new ServerSetupException("Couldn't perform initial database setup.");
        }
    }
}
