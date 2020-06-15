package com.github.sudo_sturbia.agatha.server.request;

/**
 * Request represents a client request to be handled by server.
 * <p>
 * Classes implementing Request should both verify the correctness
 * of the Request and handle its execution.
 */
public interface Request
{
    /**
     * Handle client's request and generate a response string.
     *
     * @return A JSON response to the request based on the function.
     *         Responses are documented in implementing classes.
     */
    public String handle();
}
