package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.repository.*;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RepositoryService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final GameRoomRepository gameRoomRepository;
    private final GameRoomAttendeeRepository gameRoomAttendeeRepository;
    private final KeywordRepository keywordRepository;
    private final GameStartSetRepository gameStartSetRepository;
    private ObjectMapper objectMapper = new ObjectMapper();


    //////////////TODO 댓글 관련
    // 댓글 ID로 댓글 찾아오기
    public Comment findCommentById(Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(StatusCode.COMMENT_NOT_FOUND)
        );
        return comment;
    }

    // 포스트 객체로 모든 댓글 리스트형으로 찾아오기
    public List<Comment> findAllCommentByPost(Post post){
        List<Comment> comments = commentRepository.findByPost(post);
        return comments;
    }

    // 댓글 저장
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    //////////////TODO 포스트 관련
    // 포스트 저장하기
    public Post savePost(Post post){
        postRepository.save(post);
        return post;
    }

    // 포스트 삭제하기
    public void deletePost(Post post) {
        postRepository.delete(post);
    }
    // 포스트 ID로 포스트 찾아오기
    public Post findPostById(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(StatusCode.POST_NOT_FOUND)
        );
        return post;
    }
    // 페이징 처리해서 모든 포스트 불러오기
    public Page<Post> findAllPostByPageable(Pageable pageable){
        Page<Post> postList = postRepository.findAll(pageable);
        return postList;
    }

    // 카테고리로 분류해서 모든 포스트 불러오기
    public Page<Post> findAllPostByPageableAndCategory(Pageable pageable, String category){
        Page<Post> postList = postRepository.findAllByCategoryOrderByCreatedAtDesc(pageable, category);
        return postList;
    }

    //////////////TODO GameStartSet Map <-> String
    public Map<String, String> getMapFromStr(String keywordToMember) throws JsonProcessingException {
        Map<String, String> map = objectMapper.readValue(keywordToMember, new TypeReference<Map<String, String>>() {});
        return map;
    }

    public String getStrFromMap(Map<String, String> keywordToMember) throws JsonProcessingException {
        String str = objectMapper.writeValueAsString(keywordToMember);
        return str;
    }

    //////////////TODO Member 관련
    // Email로 데이터 검증
    public boolean MemberDuplicateByEmail(String email){
        if (memberRepository.findByEmail(email).isPresent()) {
        return false; } else {
            return true;
        }
    }

    // Nickname으로 데이터 검증
    public boolean MemberDuplicateByNickname(String nickname){
        if (memberRepository.findByNickname(nickname).isPresent()) {
        return false; } else {
            return true;
        }
    }

    // 닉네임으로 Member 객체 갖고오기
    public Optional<Member> findMemberByNickname(String nickname) {
        Optional<Member> member = memberRepository.findByNickname(nickname);
        return member;
    }

    // 이메일로 Member 객체 찾아오기
    public Optional<Member> findMemberByEmail(String email){
        Optional<Member> member = memberRepository.findByEmail(email);
        return member;
    }

    // 카카오 아이디로 Member 객체 찾아오기
    public Optional<Member> findMemberByKakaoId(Long kakaoId){
        Optional<Member> member = memberRepository.findByKakaoId(kakaoId);
        return member;
    }

    // 멤버 ID로 Member 객체 찾아오기
    public Optional<Member> findMemberById(Long memberId){
        Optional<Member> member = memberRepository.findById(memberId);
        return member;
    }

    // 멤버 객체로 데이터 삭제하기
    public void deleteMember(Member member) {
        memberRepository.delete(member);
    }

    // 멤버 객체 저장하기
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    //////////////TODO GameRoom 관련
    // 게임룸 Id로 객체 찾아오기
    public Optional<GameRoom> findGameRoomByRoomId(Long roomId) {
        Optional<GameRoom> gameRoom = gameRoomRepository.findByGameRoomId(roomId);
        return gameRoom;
    }
    public Optional<GameRoom> findGameRoomByRoomIdLock(Long roomId) {
        Optional<GameRoom> gameRoom = gameRoomRepository.findByGameRoomId2(roomId);
        return gameRoom;
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

    // 게임방 저장하기
    public void saveGameRoom(GameRoom gameRoom) {
        gameRoomRepository.save(gameRoom);
    }

    // 게임방 삭제하기
    public void deleteGameRoom(GameRoom gameRoom) {
        gameRoomRepository.delete(gameRoom);
    }

    //////////////TODO GameRoomAttendee 관련
    // 멤버 객체로 참가자 정보 조회
    public GameRoomAttendee findAttendeeByMember(Member member) {
        GameRoomAttendee gameRoomAttendee = gameRoomAttendeeRepository.findByMember(member);
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

    // 게임룸 객체로 참가자 찾아오기 (GameRoom 객체가 Optional로 감싸져있는 경우)
    public List<GameRoomAttendee> findAttendeeByGameRoomOptional(Optional<GameRoom> gameRoom) {
        List<GameRoomAttendee> gameRoomAttendeeList = gameRoomAttendeeRepository.findByGameRoom(gameRoom);
        return gameRoomAttendeeList;
    }

    // 멤버 Id로 참가자 객체 가져오기
    public Optional<GameRoomAttendee> findAttendeeByMemberId(Long memberId) {
        Optional<GameRoomAttendee> gameRoomAttendee = gameRoomAttendeeRepository.findById(memberId);
        return gameRoomAttendee;
    }

    // 참가자 저장
    public void saveGameRoomAttendee(GameRoomAttendee gameRoomAttendee) {
        gameRoomAttendeeRepository.save(gameRoomAttendee);
    }

    // 참가자 삭제

    public void deleteGameRoomAttendee(GameRoomAttendee gameRoomAttendee) {
        gameRoomAttendeeRepository.delete(gameRoomAttendee);
    }

    //////////////TODO Keywrod 관련
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

    //////////////TODO GameStartSet 관련
    // GameStartSet 저장하기
    public void saveGameStartSet(GameStartSet gameStartSet) {
        gameStartSetRepository.save(gameStartSet);
    }

    // RoomId로 GameStartSet 객체 찾아오기
    public Optional<GameStartSet> findGameStartSetByRoomId(Long roomId) {
        Optional<GameStartSet> gameStartSet = gameStartSetRepository.findByRoomId(roomId);
        return gameStartSet;
    }

    // GameStartSet 객체로 DB에서 삭제하기
    public void deleteGameStartSetByRoomId(Long roomId) {
        gameStartSetRepository.deleteByRoomId(roomId);
    }
}
