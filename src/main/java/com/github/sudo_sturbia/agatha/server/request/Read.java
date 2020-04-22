package com.github.sudo_sturbia.agatha.server.request;

import java.util.regex.Pattern;

/**
 * Read handles client's READ requests. READ is used to retrieve
 * client's data. An empty READ can also be used to verify
 * client's credentials.
 * <P>
 * A READ request can be one of the following:
 * <P>
 * READ username:password // Verify client's credentials.<br/>
 * READ username:password/b/bookName // Get book.<br/>
 * READ username:password/b/* // Get a list of names all user's books.<br/>
 * READ username:password/l/labelName // Get a list of names all books with label.
 */
public class Read implements Request
{
    /** String representing request. */
    private String request;

    @Override
    public String handle()
    {
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
