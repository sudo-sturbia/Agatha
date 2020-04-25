package com.github.sudo_sturbia.agatha.server;

import java.sql.SQLException;

/**
 * Server is Agatha's server.
 * <p>
 * Server communicates with clients and handles updates to Agatha's
 * database. It also handles configuration and setup of the Agatha
 * database.
 */
public interface Server
{
    /**
     * Run the Agatha server.
     *
     * @throws SQLException if server can't get a connection to
     *         application's database or can't perform initial setup.
     */
    public void run() throws SQLException;
}
