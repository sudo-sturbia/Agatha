package com.github.sudo_sturbia.agatha.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ServerThread handles one request sent on a given socket.
 */
public class ServerThread extends Thread
{
    /** A socket to use for communication. */
    private final Socket socket;

    /** Name of Application's database. */
    private final String dbName;

    /**
     * ServerThread's constructor.
     *
     * @param socket socket to use for communication.
     * @param dbName name of application's database.
     */
    public ServerThread(Socket socket, String dbName)
    {
        super("ServerThread");
        this.socket = socket;
        this.dbName = dbName;
    }

    @Override
    public void run()
    {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null && !inputLine.isEmpty())
            {
                // Handle the request using Protocol
                String response = Protocol.handle(inputLine, this.dbName);

                // Send response to client
                out.println(response);
            }

            socket.close();
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }
}
