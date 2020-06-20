package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.core.ExecutionState;
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
 * Client requests are defined as by the following format:
 * <pre>
 *     FUNCTION username:password/object
 * </pre>
 * FUNCTION can be any of the CRUD operations, each of which is
 * represented by a class implementing Request interface. Each
 * function is documented separately in its implementing class.
 * object can be a book, note, label, or collection of books.
 * <p>
 * Due to the structure of requests, a request should not contain
 * any unnecessary : or /. A password can be sanitized by the client
 * application before sending so that : and / are replaced, but
 * sanitization should be avoided otherwise so names can be correctly
 * displayed.
 * <p>
 * Usernames and book names can only contain alphanumeric characters
 * and spaces. Any other characters are rejected.
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
