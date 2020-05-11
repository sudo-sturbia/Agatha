package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;

import java.io.IOException;
import java.net.ServerSocket;
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

    /**
     * ServerImp's constructor.
     *
     * @param dbName name of application's database.
     * @param port port to listen to.
     */
    ServerImp(String dbName, int port)
    {
        this.dbName = dbName;
        this.port = port;
    }


    @Override
    public void run() throws ServerSetupException
    {
        // Perform initial setup
        try
        {
            ConnectorBuilder.get().setup();
            this.setup();
        }
        catch (SQLException e)
        {
            throw new ServerSetupException("Connector can not be setup.");
        }


        try (ServerSocket serverSocket = new ServerSocket(this.port))
        {
            // Listen to requests
            while (true)
            {
                // Accept a socket connection, and Create a new
                // thread to handle the request.
                new ServerThread(serverSocket.accept(), this.dbName).start();
            }
        }
        catch (IOException e)
        {
            throw new ServerSetupException("Couldn't open socket.");
        }
    }

    /**
     * Perform initial database setup.
     *
     * @throws ServerSetupException if setup statements can't be executed.
     */
    private void setup() throws ServerSetupException
    {
        try (
            // Create database and users table
            Connection connection = ConnectorBuilder.get().get();
            PreparedStatement database = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS ?;");
            PreparedStatement usersTable = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS ?.Users " +
                            "(username varchar(255) NOT NULL, password varchar(64) NOT NULL, PRIMARY KEY(username));")
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
