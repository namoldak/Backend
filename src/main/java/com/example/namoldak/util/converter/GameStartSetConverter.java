package com.example.namoldak.util.converter;

import com.example.namoldak.util.GlobalResponse.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.JSON_PROCESS_FAILED;

@Component
public class GameStartSetConverter {

    private ObjectMapper objectMapper = new ObjectMapper();
    //GameStartSet Map <-> String

    public Map<String, String> getMapFromStr(String keywordToMember) {
        try {
            Map<String, String> map = objectMapper.readValue(keywordToMember, new TypeReference<Map<String, String>>() {});
            return map;
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_PROCESS_FAILED);
        }
    }

    public String getStrFromMap(Map<String, String> keywordToMember) {
        try {
            String str = objectMapper.writeValueAsString(keywordToMember);
            return str;
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_PROCESS_FAILED);
        }
    }
}
