package com.github.sudo_sturbia.agatha.server.request;

import com.google.gson.Gson;

import java.util.regex.Pattern;

/**
 * Update handles client's UPDATE requests. Update is used to
 * update objects that already exist in Agatha's database.
 * <P>
 * An UPDATE request can be one of the following:
 * <P>
 * <pre>
 *     UPDATE username:password/b/bookName/{JSON}               // Replace book with given JSON object.
 *     UPDATE username:password/b/bookName/field=updated        // Update one of book's fields.
 *     UPDATE username:password/b/bookName/n/page/{JSON}        // Replace note at page.
 *     UPDATE username:password/b/bookName/n/page/field=updated // Update one of note's fields.
 *     UPDATE username:password/l/labelName/add/b/bookName      // Add label to book.
 *     UPDATE username:password/l/labelName/remove/b/bookName   // Remove label from book.
 * </pre>
 */
public class Update implements Request
{
    /** String representing request. */
    private final String request;

    /** Name of application's database. */
    private final String dbName;

    /**
     * Update's constructor. Used only by RequestBuilder.
     *
     * @param request request to handle.
     * @param dbName name of application's database.
     */
    Update(final String request, final String dbName)
    {
        this.request = request;
        this.dbName = dbName;
    }

    /**
     * Handles the request and returns an ExecutionState object
     * in JSON.
     *
     * @return A JSON ExecutionState object.
     */
    @Override
    public String handle()
    {
        if (!this.isCorrect())
        {
            return new Gson().toJson(new ExecutionState(1)); // Wrong syntax
        }

        return null;
    }

    /**
     * Check if given request string is correct.
     *
     * @return True if request is correct, false otherwise.
     */
    private boolean isCorrect()
    {
        return Pattern.compile("^UPDATE\\s+[^:]+:[^/:]+(/(b/[^/]+/(n/[0-9]+/|)(\\{.+}|[^/=]+=[^/=]+)|l/[^/]+/(add|remove)/b/[^/]+))\\s*$",
                Pattern.CASE_INSENSITIVE).matcher(request).matches();
    }
}
