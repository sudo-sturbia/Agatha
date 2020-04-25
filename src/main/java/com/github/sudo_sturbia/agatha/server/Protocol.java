package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.server.request.Request;
import com.github.sudo_sturbia.agatha.server.request.RequestBuilder;

/**
 * Protocol is server-side handler of client's requests.
 * <p>
 * Protocol is a custom communication protocol used by Agatha for
 * client-server communication. The used protocol is stateless so
 * client's credentials are given with every request. It implements
 * the four CRUD functions for communication with the server, each
 * represented by a key word.
 * <p>
 *
 * Client requests are defined as by the following format:
 * <code>
 *     FUNCTION username:password/object
 * </code><br/>
 *
 * FUNCTION can be any of the CRUD operations, each of which is
 * represented by a class implementing Request interface. Each
 * function is documented separately in its implementing class.
 * object can be a book, note, label, or collection of books.
 * <p>
 * Due to the structure of a request,<br/>
 * username shouldn't contain a :<br/>
 * password shouldn't contain a : or /<br/>
 * object shouldn't contain any unnecessary / (other than specified.)
 * <p>
 * A password can be sanitized by the client application before sending,
 * but sanitizing should be avoided otherwise so names can be correctly
 * displayed.
 *
 * @see com.github.sudo_sturbia.agatha.server.request
 */
public class Protocol
{
    /**
     * Handle given request string, and produce a response.
     * <p>
     * If given request is correct a JSON object is returned by the
     * protocol, or "0" indicating correct execution in case of operations
     * that shouldn't return JSON.
     * Otherwise if request is wrong an error code is returned.
     *
     * @param requestString a string request.
     * @return A response, either a JSON object, or an error code
     *         indicating the state of execution.
     */
    public static String handle(String requestString)
    {
        Request request = RequestBuilder.build(requestString);
        if (request != null)
        {
            return request.handle();
        }

        return "1"; // Wrong syntax
    }
}
