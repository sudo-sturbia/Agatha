package com.github.sudo_sturbia.agatha.server.request;

import com.google.gson.Gson;

import java.util.regex.Pattern;

/**
 * Delete handles client's DELETE requests. Delete is used to
 * remove data that exists in the application's database.
 * <P>
 * A DELETE request can be one of the following:
 * <P>
 * <pre>
 *     DELETE username:password                   // Delete client from database.
 *     DELETE username:password/b/bookName        // Delete book.
 *     DELETE username:password/b/*               // Delete all books.
 *     DELETE username:password/l/labelName       // Delete label.
 *     DELETE username:password/l/*               // Delete all labels.
 *     DELETE username:password/b/bookName/n/page // Delete note at page.
 *     DELETE username:password/b/bookName/n/*    // Delete all book's notes.
 * </pre>
 */
public class Delete implements Request
{
    /** String representing request. */
    private final String request;

    /**
     * Delete's constructor. Used only by RequestBuilder.
     *
     * @param request request to handle.
     */
    Delete(final String request)
    {
        this.request = request;
    }

    /**
     * Handles the request and returns an ExecutionState object
     * in JSON.
     *
     * @param dbName name of application's database.
     * @return A JSON ExecutionState object.
     */
    @Override
    public String handle(String dbName)
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
        return Pattern.compile("^DELETE\\s+[^:]+:[^:/]+(/(b/[^/]+(/n/([0-9]+|\\*)|)|l/[^/]+)|)\\s*$",
                Pattern.CASE_INSENSITIVE).matcher(request).matches();
    }
}
