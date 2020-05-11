package com.github.sudo_sturbia.agatha.server.request;

/**
 * Request represents a client request to be handled by server.
 * <p>
 * Classes implementing Request should both verify the correctness
 * of the Request and handle its execution.
 * <p>
 * Request defines four constants representing the four CRUD functions,
 * each having a related class implementing its verification/handling.
 */
public interface Request
{
    /**
     * Constants representing CRUD functions.
     */
    public enum Function
    {
        CREATE, READ, UPDATE, DELETE
    }

    /**
     * Handle client's request and generate a response string.
     *
     * @param dbName name of application's database.
     * @return A JSON response to the request based on the function.
     *         Responses are documented in implementing classes.
     */
    public String handle(String dbName);
}
