package com.github.sudo_sturbia.agatha.server.request;

import java.util.regex.Pattern;

/**
 * Update handles client's UPDATE requests. Update is used to
 * update objects that already exist in Agatha's database.
 * <P>
 * An UPDATE request can be one of the following:
 * <P>
 * UPDATE username:password/b/bookName/{JSON} // Replace book with given JSON object.<br/>
 * UPDATE username:password/b/bookName/field=updated // Update one of book's fields.<br/>
 * UPDATE username:password/b/bookName/n/page/{JSON} // Replace note at page.<br/>
 * UPDATE username:password/b/bookName/n/page/field=updated // Update one of note's fields.<br/>
 * UPDATE username:password/l/labelName/add/b/bookName // Add label to book.<br/>
 * UPDATE username:password/l/labelName/remove/b/bookName // Remove label from book.
 */
public class Update implements Request
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
        return Pattern.compile("^UPDATE\\s+[^:]+:[^/:]+(/(b/[^/]+/(n/[0-9]+/|)(\\{.+}|[^/=]+=[^/=]+)|l/[^/]+/(add|remove)/b/[^/]+))\\s*$",
                Pattern.CASE_INSENSITIVE).matcher(request).matches();
    }
}
