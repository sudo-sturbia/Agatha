package com.github.sudo_sturbia.agatha.client.model.book;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Deserialize Note interfaces into NoteImp.
 */
public class NoteDeserializer implements JsonDeserializer<Note>
{
    @Override
    public Note deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();
        if (object.get("note") == null || object.get("pageNumber") == null)
        {
            throw new JsonParseException("Interface Note can't be unmarshalled. Fields are incomplete.");
        }

        return new NoteImp(object.get("pageNumber").getAsInt() + 1, object.get("note").getAsString(),
                object.get("pageNumber").getAsInt());
    }
}
