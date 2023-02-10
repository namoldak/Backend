package com.example.namoldak.util.converter;

import com.example.namoldak.util.GlobalResponse.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.JSON_PROCESS_FAILED;

// 기능 : 객체 전환
@Component
public class GameStartSetConverter {

    private ObjectMapper objectMapper = new ObjectMapper();
    //GameStartSet Map <-> String

    // DB에서 String 저장한 멤버와 keyword의 매칭을 Map으로 전환
    public Map<String, String> getMapFromStr(String keywordToMember) {
        try {
            Map<String, String> map = objectMapper.readValue(keywordToMember, new TypeReference<Map<String, String>>() {});
            return map;
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_PROCESS_FAILED);
        }
    }

    // DB에 String으로 저장하기 위해 멤버와 keyword의 매칭을 String으로 전환
    public String getStrFromMap(Map<String, String> keywordToMember) {
        try {
            String str = objectMapper.writeValueAsString(keywordToMember);
            return str;
        } catch (JsonProcessingException e) {
            throw new CustomException(JSON_PROCESS_FAILED);
        }
    }
}
