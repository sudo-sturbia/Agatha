package com.github.sudo_sturbia.agatha.server.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ConnectionPool is an implementation of Connector that provides
 * a pool of connections for usage.
 */
public class ConnectionPool implements Connector
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
    ConnectionPool(String dbName, String dbServerUsername, String dbServerPass)
    {
        this.dbName = dbName;
        this.dbServerUsername = dbServerUsername;
        this.dbServerPass = dbServerPass;
    }

    @Override
    public void setup() throws SQLException
    {
    }

    @Override
    public Connection get() throws SQLException
    {
        return null;
    }


    @Override
    public void close(Connection connection) throws SQLException
    {
    }
}
