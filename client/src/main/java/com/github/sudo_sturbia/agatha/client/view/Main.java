package com.github.sudo_sturbia.agatha.client.view;

import com.github.sudo_sturbia.agatha.client.model.ServerInfo;

/**
 * Main initializes fills the needed server information (server
 * host and port to bind to), and runs the client.
 */
public class Main
{
    /**
     * Parse command line arguments and run the client.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.err.println("Usage: Agatha <host> <port>");
            System.exit(1);
        }

        try
        {
            ServerInfo.setHost(args[0]);
            ServerInfo.setPort(Integer.parseInt(args[1]));
        }
        catch (NumberFormatException e)
        {
            System.err.println(e.getMessage());
        }

        View.main(args);
    }
}
