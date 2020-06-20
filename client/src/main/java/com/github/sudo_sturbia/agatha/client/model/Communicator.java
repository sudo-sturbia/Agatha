package com.github.sudo_sturbia.agatha.client.model;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Communicator handles all network related operations on the
 * client side.
 */
public class Communicator
{
    /** Client's username. Supplied by Library. */
    private final String username;

    /** Client's password. Supplied by Library. */
    private final String password;

    /** Name of server's host. */
    private final String host;

    /** Port to listen to. */
    private final int port;

    /** The four possible request functions. */
    public enum FUNCTION
    {
        CREATE, READ, UPDATE, DELETE
    }

    /**
     * Sets client's credentials.
     *
     * @param username client's username.
     * @param password client's password.
     */
    public Communicator(String username, String password, int port, String host)
    {
        this.username = username;
        this.password = password;

        this.host = host;
        this.port = port;
    }

    /**
     * Uses given parameters to construct a request of the form
     * <pre>
     *     function username:password/object
     * </pre>
     * It supplies the username and password as set by user's library.
     * The request is then sent and JSON response is unmarshalled
     * to an object of type T. An unmarshalled T object is returned.
     *
     * @param function function part of the request.
     * @param object object part of the request.
     * @param type class to use for unmarshalling JSON.
     * @param <T> Type to unmarshall server's response to.
     * @return Server's response unmarshalled to T.
     * @throws IllegalArgumentException if function is null.
     */
    public <T> T request(Class<? extends T> type, FUNCTION function, String object) throws IllegalArgumentException
    {
        String request = this.requestStr(function, object);
        String response = null;
        try (
                Socket socket = new Socket(this.host, this.port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            out.println(request); // Send request
            response = in.readLine();
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }

        return response != null ? new Gson().fromJson(response, type) : null;
    }

    /**
     * Create a request string using given parameters.
     *
     * @param function function part of the request.
     * @param object object part of the request.
     * @return A string request.
     */
    private String requestStr(FUNCTION function, String object)
    {
        StringBuilder builder = new StringBuilder();
        switch (function)
        {
            case CREATE:
                builder.append("CREATE ");
                break;
            case READ:
                builder.append("READ ");
                break;
            case UPDATE:
                builder.append("UPDATE ");
                break;
            case DELETE:
                builder.append("DELETE ");
                break;
            default:
                throw new IllegalArgumentException("Function is null.");
        }

        builder.append(this.username);
        builder.append(":");
        builder.append(this.password);
        builder.append(object);

        return builder.toString();
    }
}
