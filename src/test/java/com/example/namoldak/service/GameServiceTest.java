package com.example.namoldak.service;


import com.example.namoldak.config.QuerydslConfig;
import com.example.namoldak.repository.GameRoomRepository;
import com.example.namoldak.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class GameServiceTest {

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private MemberRepository memberRepository;


    @Autowired
    private GameService gameService;

    @Test
    public void querydsl_기본_기능_확인() {
        //given
        String nickname = "닉네임";

        //when
        String result = gameService.test(nickname);

        //then
        assertThat(result).isEqualTo("닉네임");
    }
}