package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.server.request.ExecutionState;
import com.github.sudo_sturbia.agatha.server.request.Request;
import com.github.sudo_sturbia.agatha.server.request.RequestBuilder;

import com.google.gson.Gson;

/**
 * Protocol is server-side handler of client's requests.
 * <p>
 * Protocol is a custom communication protocol used by Agatha for
 * client-server communication. Communication is done through requests
 * and responses, Responses are in JSON.
 * <p>
 * The used protocol is stateless so client's credentials are given
 * with every request. It implements the four CRUD functions for
 * communication with the server, each represented by a key word.
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
     * Request's response is a JSON object. Either the requested object
     * or an ExecutionState object.
     *
     * @param requestString a string request.
     * @param dbName name of application's database.
     * @return A JSON response.
     */
    public static String handle(String requestString, String dbName)
    {
        Request request = RequestBuilder.build(requestString, dbName);
        if (request != null)
        {
            return request.handle();
        }

        return new Gson().toJson(new ExecutionState(1)); // Wrong syntax
    }
}
