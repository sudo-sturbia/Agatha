package com.github.sudo_sturbia.agatha.server.request;

/**
 * RequestBuilder takes a request string and creates a Request
 * object to handle it.
 */
public class RequestBuilder
{
    /**
     * Build a Request object using given string.
     *
     * @param request request in string format.
     * @return A Request object to handle given request, or null
     *         if given string doesn't match any Request.
     */
    public static Request build(String request)
    {
        // Get function part of the request
        String function = request.split("\\s+", 2)[0].toLowerCase();

        switch (function)
        {
            case "create":
                return new Create(request);
            case "read":
                return new Read(request);
            case "update":
                return new Update(request);
            case "delete":
                return new Delete(request);
            default:
                return null;
        }
    }
}
