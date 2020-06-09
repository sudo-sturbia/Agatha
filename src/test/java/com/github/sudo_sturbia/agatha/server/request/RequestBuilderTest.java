package com.github.sudo_sturbia.agatha.server.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test request creation using RequestBuilder.
 */
public class RequestBuilderTest
{
    @DisplayName("Invalid requests.")
    @Test
    void invalid()
    {
        Random random = new Random();
        for (int i = 0, rand = random.nextInt(50); i < rand; i++)
        {
            assertNull(
                    RequestBuilder.build(Integer.toString(random.nextInt()), "testDB"),
                    "Book created using an invalid request."
            );
        }
    }

    @DisplayName("Valid requests.")
    @Test
    void valid()
    {
        // Create requests map
        Map<String, Class<? extends Request>> requests = new HashMap<>();

        requests.put("CREATE username:password/l/labelName", Create.class);
        requests.put("CREATE username:password",             Create.class);

        requests.put("READ username:password/b/*", Read.class);
        requests.put("READ username:password",     Read.class);

        requests.put("UPDATE username:password/b/bookName/{JSON}",        Update.class);
        requests.put("UPDATE username:password/b/bookName/field=updated", Update.class);
        requests.put("UPDATE username:password/b/bookName/n/page/{JSON}", Update.class);

        requests.put("DELETE username:password",            Delete.class);
        requests.put("DELETE username:password/b/bookName", Delete.class);
        requests.put("DELETE username:password/b/*",        Delete.class);

        for (String request : requests.keySet())
        {
            assertEquals(
                    RequestBuilder.build(request, "testDB").getClass(),
                    requests.get(request),
                    "Incorrect class created."
            );
        }
    }
}
