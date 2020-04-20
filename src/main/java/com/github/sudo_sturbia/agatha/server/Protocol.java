package com.github.sudo_sturbia.agatha.server;

/**
 * Protocol is server-side handler of client's requests.
 * <p>
 * Protocol is a custom communication protocol used by Agatha for
 * client-server communication. The used protocol is stateless so
 * client's credentials are given with every request. It implements
 * the four CRUD functions for communication with the server, each
 * represented by a key word.
 * <p>
 * Client requests are defined as by the following format:
 * <code>
 *     FUNCTION username:password/object
 * </code>
 * FUNCTION can be any of the CRUD operations, each of which is
 * represented by a class implementing Request interface. Each
 * function is documented separately in its implementing class.
 * object can be a book, note, label, or collection of books.
 */
public interface Protocol
{
    /**
     * Handle given request string, and produce a response.
     * <p>
     * If request uses an undefined function or wrong syntax/structure,
     * response is "1", otherwise the response is either a JSON object,
     * or an integer representing the state of execution. Responses
     * of each function are documented in its implementing class.
     *
     * @param request a string request.
     * @return A response, either a JSON object, or an integer based
     *         on the request.
     */
    public String handle(String request);
}
