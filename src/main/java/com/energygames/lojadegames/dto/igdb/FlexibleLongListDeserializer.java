package com.energygames.lojadegames.dto.igdb;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Deserializador flexível que pode lidar com arrays de Long ou arrays de objetos
 * onde precisamos extrair o campo "id"
 */
public class FlexibleLongListDeserializer extends JsonDeserializer<List<Long>> {

    @Override
    public List<Long> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        List<Long> result = new ArrayList<>();
        
        if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                if (parser.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
                    // É um número (ID direto)
                    result.add(parser.getLongValue());
                } else if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
                    // É um objeto, vamos extrair o campo "id"
                    JsonNode node = parser.readValueAsTree();
                    if (node.has("id")) {
                        result.add(node.get("id").asLong());
                    }
                }
            }
        }
        
        return result;
    }
}
