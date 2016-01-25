package com.goolla.serializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author szagriichuk.
 */
public class Serializer {
    private static ObjectMapper mapper = new ObjectMapper();

    public static <T> T deserialize(String data, Class<T> clazz) {
        try {
            return mapper.readValue(data, clazz);
        } catch (IOException e) {
            throw new SerializerException(e);
        }
    }

    public static JsonNode deserialize(String data) {
        try {
            return mapper.readTree(data);
        } catch (IOException e) {
            throw new SerializerException(e);
        }
    }

    public static <T> String serialize(T data) {
        try {
            return mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(data);
        } catch (IOException e) {
            throw new SerializerException(e);
        }
    }
}
