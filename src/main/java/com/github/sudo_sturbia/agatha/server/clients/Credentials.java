package com.github.sudo_sturbia.agatha.server.clients;

/**
 * Credentials is a container for client-related information.
 */
public class Credentials
{
    /** Name of application's database. */
    private final String dbName;

    /** Client's username. */
    private final String username;

    /** Client's password. */
    private final String password;

    /**
     * Create a new ClientPair with specified fields.
     *
     * @param dbName name of application's database.
     * @param username client's username.
     * @param password client's password.
     * @throws IllegalArgumentException if username is null.
     */
    public Credentials(final String dbName, final String username, final String password) throws IllegalArgumentException
    {
        if (username == null)
        {
            throw new IllegalArgumentException("No username given.");
        }
        else if (dbName == null)
        {
            throw new IllegalArgumentException("No database name given.");
        }

        this.dbName = dbName;
        this.username = username;
        this.password = password;
    }

    /**
     * Get name of application's database.
     *
     * @return name of the application's database.
     */
    public String getDatabaseName()
    {
        return this.dbName;
    }

    /**
     * Get client's username.
     *
     * @return a username.
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * Get client's password.
     *
     * @return a password.
     */
    public String getPassword()
    {
        return this.password;
    }
}
