package com.github.sudo_sturbia.agatha.server.request;

import java.util.regex.Pattern;

/**
 * Create handles client's CREATE requests. CREATE is used to
 * create users, books, labels, or notes.
 * <P>
 * A CREATE request can be one of the following:
 * <pre>
 *     CREATE username:password                     // Creates a new application user.
 *     CREATE username:password/b/{JSON}            // Creates a new book from the given JSON object.
 *     CREATE username:password/b/bookName/n/{JSON} // Creates a new note from the given JSON object.
 *     CREATE username:password/l/labelName         // Creates a new label with no books.
 * </pre>
 */
public class Create implements Request
{
    /** String representing request. */
    private final String request;

    /**
     * Create's constructor. Used only by RequestBuilder.
     *
     * @param request request to handle.
     */
    Create(final String request)
    {
        this.request = request;
    }

    /**
     * Handles the request and returns a integer response
     * representing the state of execution.
     *
     * @return A integer representing state of execution,<br/>
     *          "0" - Operation performed correctly.<br/>
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
        return Pattern.compile("^CREATE\\s+[^:]+:[^:/]+(/(b/(\\{.+}|[^/]+/n/\\{.+})|l/[^/]+)|)\\s*$",
                Pattern.CASE_INSENSITIVE).matcher(request).matches();
    }
}
