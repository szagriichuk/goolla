package com.goolla.serializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/**
 * @author szagriichuk.
 */
public class Serializer {
    public static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true).
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).
                configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

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
