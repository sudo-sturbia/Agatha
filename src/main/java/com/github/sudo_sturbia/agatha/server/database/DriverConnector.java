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
    /** Name of Application's database. */
    private final String dbName;

    /** Username for database server (MySQL). */
    private final String dbServerUsername;

    /** Password for database server (MySQL). */
    private final String dbServerPass;

    /**
     * DriverConnector's constructor.
     *
     * @param dbName name of database.
     * @param dbServerUsername username for database server (MySQL.)
     * @param dbServerPass password for database server (MySQL.)
     */
    public DriverConnector(String dbName, String dbServerUsername, String dbServerPass)
    {
        this.dbName = dbName;
        this.dbServerUsername = dbServerUsername;
        this.dbServerPass = dbServerPass;
    }

    @Override
    public void setup() throws SQLException
    {
        // no setup needed ..
    }

    @Override
    public Connection get()
    {
        try
        {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + this.dbName,
                    this.dbServerUsername, this.dbServerPass);
        }
        catch (SQLException e)
        {
            return null;
        }
    }

    @Override
    public void close(Connection connection)
    {
        try
        {
            connection.close();
        }
        catch (SQLException e)
        {
            // Couldn't close connection.
        }
    }
}
