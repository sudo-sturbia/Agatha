package com.github.sudo_sturbia.agatha.server.request;

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
     * @return Requested JSON object if request is correct, an error
     *         code otherwise.<br/>
     *          "1" - Wrong syntax/structure.<br/>
     *          "2" - Incorrect credentials.<br/>
     *          "3" - Operation failed.
     */
    @Override
    public String handle()
    {
        if (!this.isCorrect())
        {
            return "1"; // Wrong syntax
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
