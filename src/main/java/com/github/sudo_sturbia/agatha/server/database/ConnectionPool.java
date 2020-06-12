package com.github.sudo_sturbia.agatha.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ConnectionPool is an implementation of Connector that provides
 * a dynamic pool of connections.
 */
public class ConnectionPool implements Connector
{
    /** Name of Application's database. */
    private final String dbName;

    /** Username for database server (MySQL). */
    private final String dbServerUsername;

    /** Password for database server (MySQL). */
    private final String dbServerPass;

    /** Connections' pool. */
    private final List<Connection> pool;

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
        this.pool = new ArrayList<>();
    }

    @Override
    public void setup() throws SQLException
    {
        final int INITIAL_CAPACITY = 16;
        for (int i = 0; i < INITIAL_CAPACITY; i++)
        {
            this.pool.add(DriverManager.getConnection("jdbc:mysql://localhost:3306/" + this.dbName,
                            this.dbServerUsername, this.dbServerPass));
        }
    }

    @Override
    public Connection connection() throws SQLException
    {
        if (!this.pool.isEmpty())
        {
            return new CustomConnection(this.pool.remove(this.pool.size() - 1));
        }

        return new CustomConnection(
                DriverManager.getConnection("jdbc:mysql://localhost:3306/" + this.dbName,
                        this.dbServerUsername, this.dbServerPass)
        );
    }


    @Override
    public void close(Connection connection) throws SQLException
    {
        final int MAX_CAPACITY = 32;
        if (this.pool.size() < MAX_CAPACITY)
        {
            this.pool.add(connection);
        }
        else
        {
            connection.close();
        }
    }

    @Override
    public void clean()
    {
        for (Connection connection : this.pool)
        {
            try {
                connection.close();
            }
            catch (SQLException e) {
                // Ignore
            }
        }
    }
}
