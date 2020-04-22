package com.github.sudo_sturbia.agatha.server.request;

import java.util.regex.Pattern;

/**
 * Delete handles client's DELETE requests. Delete is used to
 * remove data that exists in the application's database.
 * <P>
 * A DELETE request can be one of the following:
 * <P>
 * DELETE username:password // Delete client from database.<br/>
 * DELETE username:password/b/bookName // Delete book.<br/>
 * DELETE username:password/b/* // Delete all books.<br/>
 * DELETE username:password/l/labelName // Delete label.<br/>
 * DELETE username:password/l/* // Delete all labels.<br/>
 * DELETE username:password/b/bookName/n/page // Delete note at page.<br/>
 * DELETE username:password/b/bookName/n/* // Delete all book's notes.
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
        return Pattern.compile("^DELETE\\s+[^:]+:[^:/]+(/(b/[^/]+(/n/([0-9]+|\\*)|)|l/[^/]+)|)\\s*$",
                Pattern.CASE_INSENSITIVE).matcher(request).matches();
    }
}
