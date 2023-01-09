package com.example.namoldak.service;

import com.example.namoldak.domain.Comment;
import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import com.example.namoldak.dto.RequestDto.PostRequestDto;
import com.example.namoldak.dto.ResponseDto.CommentResponseDto;
import com.example.namoldak.dto.ResponseDto.PostResponseDto;
import com.example.namoldak.dto.ResponseDto.PrivateResponseBody;
import com.example.namoldak.repository.PostRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

// 기능 : 포스트 CRUD 서비스
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    // 포스트 생성
    @Transactional
    public PrivateResponseBody addPost(PostRequestDto postRequestDto, Member member) {
        Post post = postRepository.save(new Post(postRequestDto, member));
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for (Comment comment : post.getCommentList()) {
            commentResponseDtoList.add(new CommentResponseDto(comment));
        }
        return new PrivateResponseBody<>(StatusCode.OK, "포스트 생성이 완료되었습니다.");
    }

    // 포스트 전체 조회
    @Transactional
    public Page<PostResponseDto> getAllPost(Pageable pageable) {
        Page<Post> postList = postRepository.findAll(pageable);
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Post post : postList) {
            postResponseDtoList.add(new PostResponseDto(post));
        }
        final Page<PostResponseDto> page = new PageImpl<>(postResponseDtoList);
        return page;
    }

    // 포스트 수정
    @Transactional
    public PrivateResponseBody updatePost(Long id, PostRequestDto postRequestDto, Member member) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new CustomException(StatusCode.POST_ERROR)
        );
        if (member.getId().equals(post.getMember().getId())) {
            post.update(postRequestDto);
            return new PrivateResponseBody<>(StatusCode.OK, "포스트 수정 완료");
        } else {
            throw new CustomException(StatusCode.NO_AUTH_MEMBER);
        }
    }

    @Transactional
    public PrivateResponseBody deletePost(Long id, Member member) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new CustomException(StatusCode.POST_ERROR)
        );
        if (post.getMember().getId().equals(member.getId())) {
            postRepository.delete(post);
        } else {
            throw new CustomException(StatusCode.NO_AUTH_MEMBER);
        }
        return new PrivateResponseBody<>(StatusCode.OK, "포스트 삭제가 완료되었습니다.");
    }

}
