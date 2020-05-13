package com.github.sudo_sturbia.agatha.server.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connector provides database connections to be used by
 * Agatha's server.
 * <p>
 * The interface is meant to not be too specific so that it
 * can be implemented in different ways e.g. a connection pool or
 * a simple wrapper around DriverManager.getConnection.
 */
public interface Connector
{
    /**
     * Perform any initial setup, if there's any.
     *
     * @throws SQLException in case of a SQL error.
     */
    public void setup() throws SQLException;

    /**
     * Get a database connection.
     *
     * @return A Connection object connected to application's database.
     * @throws SQLException in case of a SQL error.
     */
    public Connection get() throws SQLException;

    /**
     * Close or return a database connection.
     *
     * @param connection database connection to close.
     * @throws SQLException in case of a SQL error.
     */
    void close(Connection connection) throws SQLException;
}
