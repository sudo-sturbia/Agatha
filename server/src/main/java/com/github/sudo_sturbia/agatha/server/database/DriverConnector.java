package com.github.sudo_sturbia.agatha.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DriverConnector is a wrapper around DriverManager to use
 * Connector interface.
 */
public class DriverConnector implements Connector
{
    /** Username for database server (MySQL). */
    private final String dbServerUsername;

    /** Password for database server (MySQL). */
    private final String dbServerPass;

    /**
     * DriverConnector's constructor.
     *
     * @param dbServerUsername username for database server (MySQL.)
     * @param dbServerPass password for database server (MySQL.)
     */
    DriverConnector(String dbServerUsername, String dbServerPass)
    {
        this.dbServerUsername = dbServerUsername;
        this.dbServerPass = dbServerPass;
    }

    @Override
    public void setup() throws SQLException
    {
        // No setup needed.
    }

    @Override
    public Connection connection() throws SQLException
    {
        // Return a CustomConnection
        return new CustomConnection(
                DriverManager.getConnection("jdbc:mysql://localhost:3306/",
                        this.dbServerUsername, this.dbServerPass)
        );
    }

    @Override
    public void close(Connection connection) throws SQLException
    {
        connection.close();
    }

    @Override
    public void clean()
    {
        // no cleaning needed.
    }
}
