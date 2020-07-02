package com.github.sudo_sturbia.agatha.client.model;

import com.github.sudo_sturbia.agatha.core.BookState;
import com.github.sudo_sturbia.agatha.core.BookStateDeserializer;
import com.github.sudo_sturbia.agatha.core.ExecutionState;
import com.github.sudo_sturbia.agatha.core.Note;
import com.github.sudo_sturbia.agatha.core.NoteDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

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
     * Get client's username.
     *
     * @return Client's username, given by the library.
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * Verifies that a server is running at the specified host on the
     * specified port. Sends an incorrect request and verifies that
     * response is an ExecutionState object with the correct error code.
     *
     * @return True if the server is running at the specified port,
     *         False otherwise.
     */
    public boolean isServerRunning()
    {
        try (
                Socket socket = new Socket(this.host, this.port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            out.println("Gibberish."); // Send request
            String response = in.readLine();
            if (new Gson().fromJson(response, ExecutionState.class).getCode() == 1)
            {
                return true;
            }
        }
        catch (IOException | JsonParseException e)
        {
            // Request failed ..
        }

        return false;
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
        Gson gson = new GsonBuilder().registerTypeAdapter(BookState.class, new BookStateDeserializer())
                                     .registerTypeAdapter(Note.class, new NoteDeserializer())
                                     .create();

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

        try
        {
            return response != null ? gson.fromJson(response, type) : null;
        }
        catch (JsonSyntaxException e)
        {
            return null;
        }
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
