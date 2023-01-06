package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.repository.GameRoomMemberRepository;
import com.example.namoldak.repository.GameRoomRepository;
import com.example.namoldak.repository.GameStartSetRepository2;
import com.example.namoldak.repository.MemberRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.List;

import static com.example.namoldak.domain.QGameRoom.gameRoom;
import static com.example.namoldak.domain.QGameRoomMember.gameRoomMember;
import static com.example.namoldak.domain.QMember.member;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameService {

    private final SimpMessageSendingOperations messagingTemplate;

    private final MemberRepository memberRepository;
    private final GameRoomRepository gameRoomRepository;
    private final GameStartSetRepository2 gameStartSetRepository2;
    private final GameRoomMemberRepository gameRoomMemberRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager em;

    // 건너뛰기
    @Transactional
    public void gameSkip(Member member, Long gameroomid) {

        // stomp로 메세지 전달
        GameMessage gameMessage = new GameMessage();
        gameMessage.setRoomId(Long.toString(gameroomid)); // 현재 방 id
        gameMessage.setSenderId(String.valueOf(member.getId())); // 로그인한 유저의 id
        gameMessage.setSender(member.getNickname()); // 로그인한 유저의 닉네임
        gameMessage.setContent(gameMessage.getSender() + "님이 건너뛰기를 선택하셨습니다.");
        gameMessage.setType(GameMessage.MessageType.SKIP);

        // 방 안의 구독자 모두가 메세지 받음
        messagingTemplate.convertAndSend("/sub/gameroom/" + gameroomid, gameMessage);
    }

    public GameStartSet2 spotlight(Long gameRoomId){
        log.info("1 what");
        log.info(String.valueOf(gameRoomId));

        Member member2 = jpaQueryFactory
                .selectFrom(member)
                .where(member.id.eq(6L))
                .fetchOne();

        System.out.println(member2.getEmail());

        // 게임룸 조회 (게임룸 상태를 조회하기 위한 조회)
        GameRoom playRoom = jpaQueryFactory
                .selectFrom(gameRoom)
                .where(gameRoom.gameRoomName.eq("테스트방5"))
                .fetchOne();

        em.flush();
        em.clear();


        log.info("2");
        // 해당 게임룸의 게임셋을 조회
        GameStartSet2 gameStartSet = gameStartSetRepository2.findGameSetById(gameRoomId);

        log.info("3");
        // 게임이 진행이 불가한 상태라면 초기화 시켜야 함
        if(playRoom.getStatus().equals("false")){ // false? true? 여튼 게임 진행이 아닌 상태
            gameStartSet.setRound(0);
            gameStartSet.setSpotNum(0);
            gameStartSetRepository2.saveGameSet(gameStartSet);
        }

        log.info("4");
        // 유저들 정보 조회
        List<GameRoomMember> memberListInGame = jpaQueryFactory
                .selectFrom(gameRoomMember)
                .where(gameRoomMember.gameRoom.gameRoomId.eq(gameRoomId))
                .orderBy(gameRoomMember.createdAt.asc())
                .fetch();

        log.info("5");
        // 라운드 진행 중
        if(gameStartSet.getSpotnum() < memberListInGame.size()){

            // 현재 스포트라이트 받는 멤버
            Member spotMember = jpaQueryFactory
                    .selectFrom(member)
                    .where(member.id.eq(memberListInGame.get(gameStartSet.getSpotnum()).getMember().getId()))
                    .fetchOne();

            log.info("6");
            // 메세지 알림
            GameMessage gameMessage = new GameMessage();
            gameMessage.setRoomId(Long.toString(gameRoomId));                   // 현재 게임룸 id
            gameMessage.setSenderId(String.valueOf(spotMember.getId()));        // 스포트라이트 멤버 id
            gameMessage.setSender(spotMember.getNickname());                    // 스포트라이트 멤버 닉네임
            gameMessage.setContent(gameMessage.getSender() + "님이 차례입니다.");  // 메세지
            gameMessage.setType(GameMessage.MessageType.SPOTLIGHT);

            messagingTemplate.convertAndSend("/sub/gameroom/" + gameRoomId, gameMessage);

            log.info("7");
            // 다음 차례로!
            gameStartSet.setSpotNum(gameStartSet.getSpotnum() + 1);
            gameStartSetRepository2.saveGameSet(gameStartSet);


        } else if (gameStartSet.getSpotnum() == memberListInGame.size()) {


            if (gameStartSet.getRound() < 20) {
                log.info("8");
                // 한 라운드 종료, 라운드 +1 , 위치 정보 초기화
                gameStartSet.setRound(gameStartSet.getRound() + 1);
                gameStartSet.setSpotNum(0);

            } else if (gameStartSet.getRound() == 20) {
                log.info("9");
                // 메세지 알림 = 여기 말할 이야기
                GameMessage gameMessage = new GameMessage();
                gameMessage.setRoomId(Long.toString(gameRoomId));               // 현재 게임룸 id
                gameMessage.setSenderId("admin");
                gameMessage.setSender("양계장 주인");
                gameMessage.setContent("20라운드까지 정답자가 안나오다니!!!!!");
                gameMessage.setType(GameMessage.MessageType.SKIP);

                messagingTemplate.convertAndSend("/sub/gameroom/" + gameRoomId, gameMessage);

            }
        }

        return gameStartSet;
    }

    public String test(String nickname){
        // 게임룸 조회 (게임룸 상태를 조회하기 위한 조회)

        QMember qMember = member;

        Member member3 = jpaQueryFactory
                .selectFrom(member)
                .where(
                        member.nickname.eq(nickname)
                )
                .fetchOne();

        return nickname;
    }
}
