package com.github.sudo_sturbia.agatha.server;

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
     * @throws ServerSetupException if a problem occurs while setting
     *         up application's server (or database.)
     */
    public void run() throws ServerSetupException;
}
