package com.example.namoldak.service;

import com.example.namoldak.repository.GameRoomRepository;
import com.example.namoldak.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class GameServiceTest {

    @Test
    public void querydsl_기본_기능_확인() {
        //given
        Map<String, String> testMap = new HashMap<>();
        testMap.put("test1", "1");
        testMap.put("test2", "2");
        testMap.put("test3", "3");
        testMap.put("test4", "4");

        String test = "";
        for(Map.Entry<String, String> oneTestMap : testMap.entrySet()){
            if(oneTestMap.getKey().equals("test2")){
                test = oneTestMap.getKey();
            }
        }
        testMap.remove(test);

        for(Map.Entry<String, String> twoTestMap : testMap.entrySet()){
            System.out.println(twoTestMap.getKey());
        }
    }
}