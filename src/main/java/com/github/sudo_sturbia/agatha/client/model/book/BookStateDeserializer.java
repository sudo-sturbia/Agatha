package com.github.sudo_sturbia.agatha.client.model.book;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * BookStateDeserializer is used to deserialize BookState interface
 * to a specific class based on BookState's TYPE constant.
 */
public class BookStateDeserializer implements JsonDeserializer<BookState>
{
    @Override
    public BookState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();

        if (object.get("TYPE") == null)
        {
            throw new JsonParseException("Interface BookState can't be unmarshalled. TYPE doesn't exist.");
        }

        switch (object.get("TYPE").getAsString())
        {
            case "INTERESTED":
                return new InterestedState(object.get("pages").getAsInt());
            case "CURRENTLY_READING":
                return new ReadingState(object.get("pages").getAsInt(), object.get("readPages").getAsInt());
            case "READ":
                return new ReadState(object.get("pages").getAsInt());
            default:
                throw new JsonParseException("Invalid TYPE for interface BookState.");
        }
    }
}
