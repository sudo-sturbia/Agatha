package com.github.sudo_sturbia.agatha.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ServerThread handles a request
 */
public class ServerThread extends Thread
{
    /** A socket to use for communication. */
    private final Socket socket;

    /**
     * ServerThread's constructor.
     *
     * @param socket socket to use for communication.
     */
    public ServerThread(Socket socket)
    {
        super("ServerThread");
        this.socket = socket;
    }

    @Override
    public void run()
    {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            // Read request from socket, and handle it using Protocol
            String response = Protocol.handle(in.readLine());

            // Send response to client
            out.println(response);

            socket.close();
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }
}
