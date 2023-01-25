package com.example.namoldak.service;

import com.example.namoldak.domain.Comment;
import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import com.example.namoldak.dto.RequestDto.PostRequestDto;
import com.example.namoldak.dto.ResponseDto.*;
import com.example.namoldak.repository.CommentRepository;
import com.example.namoldak.repository.PostRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 기능 : 포스트 CRUD 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final S3Uploader s3Uploader;
    private final RepositoryService repositoryService;


    // 포스트 생성
    @Transactional
    public PostResponseDto addPost(PostRequestDto postRequestDto, MultipartFile multipartFile, Member member) throws IOException {
        String image = null;
        Post post;
        if (multipartFile != null) {
            image = s3Uploader.upload(multipartFile, "static");
            post = postRepository.save(new Post(postRequestDto, image, member));
        } else {
            post = postRepository.save(new Post(postRequestDto, "", member));
        }

        return new PostResponseDto(post, image);
    }

    // 포스트 전체 조회
    @Transactional
    public PostResponseListDto getAllPost(Pageable pageable) {
        Page<Post> postList = repositoryService.findAllPostByPageable(pageable);
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Post post : postList) {
            postResponseDtoList.add(new PostResponseDto(post));
        }
        int totalPage = postList.getTotalPages();
        return new PostResponseListDto(totalPage, postResponseDtoList);
    }

    // 카테고리별 포스트 조회
    public PostResponseListDto getCategoryPost(Pageable pageable, String category) {
        Page<Post> postList = repositoryService.findAllPostByPageableAndCategory(pageable, category);
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Post post : postList) {
            postResponseDtoList.add(new PostResponseDto(post));
        }

        int totalPage = postList.getTotalPages();
        return new PostResponseListDto(totalPage, postResponseDtoList);
    }

    // 포스트 상세 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getOnePost(Long id) {
        List<PostResponseDto> result = new ArrayList<>();
        Post post = repositoryService.findPostById(id);

        String image = post.getImageFile();

        List<Comment> comments = repositoryService.findAllCommentByPost(post);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponseDtoList.add(new CommentResponseDto(comment));
        }

        result.add(new PostResponseDto(post, image, commentResponseDtoList));
        return result;
    }

    // 포스트 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto, MultipartFile multipartFile, Member member) throws IOException {
        Post post = repositoryService.findPostById(id);
        if (member.getId().equals(post.getMember().getId())) {
            post.update(postRequestDto);
        } else {
            throw new CustomException(StatusCode.NO_AUTH_MEMBER);
        }

        String image = null;
        if (!multipartFile.isEmpty()) {
            image = (s3Uploader.upload(multipartFile, "static"));
            Post post1 = postRepository.findById(id).orElseThrow();
            s3Uploader.delete(post1.getImageFile());
            post1.update(image);
        }
        return new PostResponseDto(post, image);
    }

    // 포스트 삭제
    @Transactional
    public void deletePost(Long id, Member member) {
        Post post = repositoryService.findPostById(id);
        if (post.getMember().getId().equals(member.getId())) {
            Post post1 = postRepository.findById(id).orElseThrow();
            s3Uploader.delete(post1.getImageFile());
            repositoryService.deletePost(post);
        } else {
            throw new CustomException(StatusCode.NO_AUTH_MEMBER);
        }
    }
}
