package com.example.demo.validation;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Map;

public class ItemDictionary {

    private static final String DICTIONARY_FILE = "/metadata/column-dictionary.json";

    private static Map<String, ItemDefinition> dictionary;

    static {
        load();
    }

    private static void load() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = ItemDictionary.class.getResourceAsStream(DICTIONARY_FILE);
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            dictionary = mapper.readValue(
                    is,
                    mapper.getTypeFactory().constructMapType(Map.class, String.class, ItemDefinition.class)
            );
        } catch (Exception e) {
            System.out.println("Failed to load column dictionary: " + e.getMessage());
            throw new RuntimeException("Failed to load column dictionary", e);
        }
    }

    public static ItemDefinition get(String key) {
        return dictionary.get(key);
    }
}