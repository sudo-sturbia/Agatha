package com.github.sudo_sturbia.agatha.server.request;

import com.github.sudo_sturbia.agatha.server.clients.ClientManager;
import com.google.gson.Gson;

/**
 * RequestUtil contains utility methods used by classes implementing
 * Request.
 */
public class RequestUtil
{
    /**
     * Takes an array of strings that should contain a username as the
     * first element, and password as the second element. Verifies
     * both the size of the given array against a given size, and the
     * existence of username/password.
     *
     * @param dbName name of application's database.
     * @param list an array of strings.
     * @param size size to compare array's size to.
     * @return
     *      A JSON ExecutionState object with code 3 if sizes don't match.
     *      A JSON ExecutionState object with code 2 if credentials are wrong.
     *      Null otherwise.
     */
    public static String verify(String dbName, String[] list, int size)
    {
        if (list.length != size)
        {
            return new Gson().toJson(new ExecutionState(3)); // Operation failed
        }

        // Verify username and password
        return (!ClientManager.get().doesExist(dbName, list[0], list[1])) ?
                new Gson().toJson(new ExecutionState(2)) :
                null;
    }
}
