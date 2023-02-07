package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.repository.*;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.s3.AwsS3Service;
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

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.*;

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
    private final RewardReposiroty rewardReposiroty;
    private final ImageFileRepository imageFileRepository;
    private final AwsS3Service awsS3Service;
}
