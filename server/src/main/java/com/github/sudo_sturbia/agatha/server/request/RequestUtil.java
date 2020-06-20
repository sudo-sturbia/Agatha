package com.github.sudo_sturbia.agatha.server.request;

import com.github.sudo_sturbia.agatha.core.ExecutionState;
import com.github.sudo_sturbia.agatha.server.clients.ClientManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RequestUtil is a container for utility methods used by classes
 * implementing Request interface.
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
        if (list == null || list.length != size)
        {
            return new Gson().toJson(new ExecutionState(1));
        }

        switch (ClientManager.get().doesExist(dbName, list[0], list[1]))
        {
            case SUCCEEDED:
                return null; // Succeeded
            case TIMEOUT:
                return new Gson().toJson(new ExecutionState(4)); // Account locked
            case FAILED:
            default: // Can't happen
                return new Gson().toJson(new ExecutionState(2)); // Wrong credentials
        }
    }

    /**
     * Remove empty strings from an array.
     *
     * @param list array to remove empty strings from.
     * @return list with no empty strings.
     */
    public static String[] removeEmpty(String[] list)
    {
        List<String> removeFrom = new ArrayList<>(Arrays.asList(list));
        removeFrom.removeIf(String::isEmpty);
        return removeFrom.toArray(new String[0]);
    }

    /**
     * Split given string str twice, first use reg1 to split, then
     * split the first part of the split string using reg2.
     * This method is created to be used by request strings which
     * contains JSON data to avoid splitting the JSON string.
     * <p>
     * Returned array has no empty elements.
     *
     * @param str string to split.
     * @param reg1 regex pattern to use in first split.
     * @param reg2 regex pattern to use in second split.
     * @return An array of split parts, null if something an error
     *      occurs while splitting.
     */
    public static String[] splitTwice(String str, String reg1, String reg2)
    {
        // First split
        List<String> split = new ArrayList<>(Arrays.asList(str.split(reg1)));

        if (split.isEmpty())
        {
            return null;
        }

        // Second split of first element of list
        split.addAll(0, new ArrayList<>(Arrays.asList(split.remove(0).split(reg2))));
        split.removeIf(String::isEmpty);

        return split.toArray(new String[0]);
    }
}
