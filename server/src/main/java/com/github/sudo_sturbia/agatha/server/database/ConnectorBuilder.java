package com.github.sudo_sturbia.agatha.server.database;

/**
 * ConnectorBuilder handles the creation of a Connector object
 * and insures that only one Connector exists at any time.
 */
public class ConnectorBuilder
{
    /** Available types of connectors. */
    public enum ConnectorType
    {
        NORMAL, POOL
    }

    /** Database connector. */
    private static Connector connector;

    /**
     * Create a singleton connector object with specified fields.
     * Should be called only once.
     *
     * @param type type of connector.
     * @param dbServerUsername username for database server (MySQL).
     * @param dbServerPass password for database server (MySQL).
     * @throws IllegalArgumentException if any given argument is null.
     * @throws IllegalStateException if method was called before.
     */
    public static void setup(ConnectorType type, String dbServerUsername, String dbServerPass)
            throws IllegalArgumentException, IllegalStateException
    {
        // If method was called before
        if (connector != null)
        {
            throw new IllegalStateException("Attempting to reassign connector.");
        }

        if (type == null)
        {
            throw new IllegalArgumentException("Connector type is not given.");
        }
        else if (dbServerUsername == null)
        {
            throw new IllegalArgumentException("Database server username is not given.");
        }
        else if (dbServerPass == null)
        {
            throw new IllegalArgumentException("Database server password is not given.");
        }

        connector = type == ConnectorType.POOL ?
                new ConnectionPool(dbServerUsername, dbServerPass) :
                new DriverConnector(dbServerUsername, dbServerPass);
    }

    /**
     * Get singleton connector.
     *
     * @return A Connector object.
     * @throws IllegalStateException if connector is null (not setup).
     */
    public static Connector connector() throws IllegalStateException
    {
        if (connector == null)
        {
            throw new IllegalStateException("No connector exists.");
        }

        return ConnectorBuilder.connector;
    }
}
