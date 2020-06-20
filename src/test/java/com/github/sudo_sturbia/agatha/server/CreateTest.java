package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.client.model.book.Book;
import com.github.sudo_sturbia.agatha.client.model.book.BookBuilder;
import com.github.sudo_sturbia.agatha.client.model.book.Note;
import com.github.sudo_sturbia.agatha.client.model.book.NoteImp;
import com.github.sudo_sturbia.agatha.server.request.ExecutionState;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test handling of CREATE requests.
 */
public class CreateTest
{
    @BeforeAll
    static void setup()
    {
        TestUtil.setup();
    }

    @DisplayName("Test CREATE operations.")
    @Test
    void create()
    {
        final String dbName = "testDB";
        final Gson gson = new Gson();

        Book book = BookBuilder.newBook("My Book", 100).build();
        Note note = new NoteImp(book.getNumberOfPages(), "Note #1", 10);

        List<String> requests = new ArrayList<>();
        requests.add("CREATE username:password");
        requests.add("CREATE username:password/b/" + gson.toJson(book));
        requests.add("CREATE username:password/b/" + book.getName() + "/n/" + gson.toJson(note));
        requests.add("CREATE username:password/l/label");

        for (String request : requests)
        {
            String response = Protocol.handle(request, dbName);
            assertEquals(0, gson.fromJson(response, ExecutionState.class).getCode(),
                    String.format("Request \"%s\" failed.", request));
        }
    }

    @AfterAll
    static void clean()
    {
        TestUtil.clean();
    }
}
