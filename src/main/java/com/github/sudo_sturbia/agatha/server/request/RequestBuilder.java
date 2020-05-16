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
     * @param dbName name of application's database.
     * @return A Request object to handle given request, or null
     *         if given string doesn't match any Request.
     */
    public static Request build(String request, String dbName)
    {
        // Get function part of the request
        String function = request.split("\\s+", 2)[0].toLowerCase();

        switch (function)
        {
            case "create":
                return new Create(request, dbName);
            case "read":
                return new Read(request, dbName);
            case "update":
                return new Update(request, dbName);
            case "delete":
                return new Delete(request, dbName);
            default:
                return null;
        }
    }
}
