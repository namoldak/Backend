package com.example.namoldak.domainModel;

import com.example.namoldak.domain.*;
import com.example.namoldak.repository.*;
import com.example.namoldak.util.GlobalResponse.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.*;
import static com.example.namoldak.util.GlobalResponse.code.StatusCode.NOT_EXIST_ROOMS;

@Component
@RequiredArgsConstructor
public class GameQuery {

    private final GameStartSetRepository gameStartSetRepository;
    private final KeywordRepository keywordRepository;
    private final GameRoomAttendeeRepository gameRoomAttendeeRepository;
    private final GameRoomRepository gameRoomRepository;
    private final RewardReposiroty rewardReposiroty;

    //////////////TODO GameRoom 관련
    // 게임룸 Id로 객체 찾아오기
    public GameRoom findGameRoomByRoomId(Long roomId) {
        return gameRoomRepository.findByGameRoomId(roomId).orElseThrow(
                ()-> new CustomException(NOT_EXIST_ROOMS)
        );
    }
    public GameRoom findGameRoomByRoomIdLock(Long roomId) {
        return gameRoomRepository.findByGameRoomId2(roomId).orElseThrow(
                ()-> new CustomException(NOT_EXIST_ROOMS)
        );
    }

    // 페이징 처리해서 모든 게임방 갖고 오기
    public Page<GameRoom> findGameRoomByPageable(Pageable pageable) {
        Page<GameRoom> gameRoomPage = gameRoomRepository.findAll(pageable);
        return gameRoomPage;
    }

    // 특정 키워드로 검색해서 게임방 가져오기
    public Page<GameRoom> findGameRoomByContainingKeyword(Pageable pageable, String keyword) {
        Page<GameRoom> gameRoomPage = gameRoomRepository.findByGameRoomNameContaining(pageable, keyword);
        return gameRoomPage;
    }

    // 게임방 리스트 형식으로 갖고 오기
    public List<GameRoom> findAllGameRoomList() {
        List<GameRoom> gameRoomList = gameRoomRepository.findAll();
        return gameRoomList;
    }


    //////////////TODO GameRoomAttendee 관련
    // 멤버 객체로 참가자 정보 조회
    public GameRoomAttendee findAttendeeByMember(Member member) {
        GameRoomAttendee gameRoomAttendee = gameRoomAttendeeRepository.findByMember(member).orElseThrow(
                ()-> new CustomException(NOT_FOUND_ATTENDEE)
        );
        return gameRoomAttendee;
    }
    // 게임룸 객체로 참가자 찾아오기
    public List<GameRoomAttendee> findAttendeeByGameRoom(GameRoom gameRoom) {
        List<GameRoomAttendee> gameRoomAttendeeList = gameRoomAttendeeRepository.findByGameRoom(gameRoom);
        return gameRoomAttendeeList;
    }

    public List<GameRoomAttendee> findAttendeeByRoomId(Long roomId) {
        return gameRoomAttendeeRepository.findByGameRoom_GameRoomId(roomId);
    }

    // 멤버 Id로 참가자 객체 가져오기
    public GameRoomAttendee findAttendeeByMemberId(Long memberId) {
        return gameRoomAttendeeRepository.findById(memberId).orElseThrow(
                ()-> new CustomException(NOT_FOUND_ATTENDEE)
        );
    }

    //////////////TODO GameStartSet 관련
    // RoomId로 GameStartSet 객체 찾아오기
    public GameStartSet findGameStartSetByRoomId(Long roomId) {
        return gameStartSetRepository.findByRoomId(roomId).orElseThrow(
                ()-> new CustomException(GAME_SET_NOT_FOUND)
        );
    }

    //////////////TODO 댓글 관련
    // 키워드 랜덤으로 4개 가지고 오기
    public List<Keyword> findTop4KeywordByCategory(String category) {
        List<Keyword> keywordList = keywordRepository.findTop4ByCategory(category);
        return keywordList;
    }

    // 키워드 랜덤으로 3개 가지고 오기
    public List<Keyword> findTop3KeywordByCategory(String category) {
        List<Keyword> keywordList = keywordRepository.findTop3ByCategory(category);
        return keywordList;
    }

    //////////////TODO Reward 관련
    public List<Reward> findAllReward(Member member) {
        List<Reward> rewardList = rewardReposiroty.findByMember(member);
        return rewardList;
    }
}
