package com.github.sudo_sturbia.agatha.server.request;

import com.google.gson.Gson;

import java.util.regex.Pattern;

/**
 * Read handles client's READ requests. READ is used to retrieve
 * client's data. An empty READ can also be used to verify
 * client's credentials.
 * <P>
 * A READ request can be one of the following:
 * <P>
 * <pre>
 *     READ username:password             // Verify client's credentials.
 *     READ username:password/b/bookName  // Get book.
 *     READ username:password/b/*         // Get a list of names all user's books.
 *     READ username:password/l/labelName // Get a list of names all books with label.
 * </pre>
 */
public class Read implements Request
{
    /** String representing request. */
    private final String request;

    /**
     * Read's constructor. Used only by RequestBuilder.
     *
     * @param request request to handle.
     */
    Read(final String request)
    {
        this.request = request;
    }

    /**
     * Handles the request and returns a JSON object.
     *
     * @param dbName name of application's database.
     * @return Requested JSON object if request is correct, an
     *         ExecutionState object otherwise.
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
        return Pattern.compile("^READ\\s+[^:]+:[^:/]+(/(b/([^/]+|\\*)|l/[^/]+)|)$",
                Pattern.CASE_INSENSITIVE).matcher(request).matches();
    }
}
