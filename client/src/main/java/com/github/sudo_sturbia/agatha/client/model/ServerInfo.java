package com.github.sudo_sturbia.agatha.client.model;

/** A container for server information. */
public class ServerInfo
{
    /** Server host. */
    private static String host;

    /** Server port. */
    private static int port;

    /**
     * Set name of Agatha's server host.
     *
     * @param host server host.
     */
    public static void setHost(String host)
    {
        ServerInfo.host = host;
    }

    /**
     * Set server's port number.
     *
     * @param port server's port number.
     */
    public static void setPort(int port)
    {
        ServerInfo.port = port;
    }

    /**
     * Get host of Agatha's server.
     *
     * @return name of Agatha's server host.
     */
    public static String getHost()
    {
        return ServerInfo.host;
    }

    /**
     * Get port number to connect to on server's host.
     *
     * @return port number to connect to.
     */
    public static int getPort()
    {
        return ServerInfo.port;
    }
}
