package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.domainModel.PostCommand;
import com.example.namoldak.domainModel.PostQuery;
import com.example.namoldak.dto.RequestDto.PostRequestDto;
import com.example.namoldak.dto.ResponseDto.*;
import com.example.namoldak.repository.ImageFileRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.s3.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

// 기능 : 포스트 CRUD 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostQuery postQuery;
    private final PostCommand postCommand;
    private final AwsS3Uploader awsS3Uploader;
    private final ImageFileRepository imageFileRepository;


    // 포스트 생성
    @Transactional
    public PostResponseDto addPost(PostRequestDto postRequestDto, List<MultipartFile> multipartFilelist, Member member) {
        Post post = new Post(postRequestDto, member);
        postCommand.savePost(post);

        if (multipartFilelist != null) {
            awsS3Uploader.upload(multipartFilelist, "static", post, member);
        }

        List<ImageFile> imageFiles = imageFileRepository.findAllByPost(post);
        List<String> imagePath = new ArrayList();
        for (ImageFile imageFile : imageFiles) {
            imagePath.add(imageFile.getPath());
        }
        PostResponseDto postResponseDto = new PostResponseDto(post, imagePath);

        return postResponseDto;
    }

    // 자유게시판 전체 조회
    public PostResponseListDto getFreeBoard(Pageable pageable, String category) {
        Page<Post> postList = postQuery.findAllByCategory(pageable, category);
        List<Post> posts = postQuery.findAllByCategory(category);

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for (Post post : postList) {
            postResponseDtoList.add(new PostResponseDto(post));
        }
        int totalPage = postList.getTotalPages();

        int postCnt = posts.size();
        return new PostResponseListDto(totalPage, postCnt, postResponseDtoList);
    }

    // 내가쓴피드백 전체 조회
    public PostResponseListDto getMyPost(Pageable pageable, Member member, String category) {
        Page<Post> postList = postQuery.findAllByMemberAndCategoryOrderByCreatedAtDesc(pageable, member, category);
        List<Post> posts = postQuery.findAllByMemberAndCategory(member, category); // memberid

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for (Post post : postList) {
            postResponseDtoList.add(new PostResponseDto(post));
        }
        int totalPage = postList.getTotalPages();
        int postCnt = posts.size();
        return new PostResponseListDto(totalPage, postCnt, postResponseDtoList);
    }

    // 포스트 상세 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getOnePost(Long id) {
        Post post = postQuery.findPostById(id);
        List<PostResponseDto> result = new ArrayList<>();

        List<String> imageFileList = new ArrayList<>();
        for (ImageFile imageFile : post.getImageFileList()) {
            imageFileList.add(imageFile.getPath());
        }

        result.add(new PostResponseDto(post, imageFileList));
        return result;
    }

    // 포스트 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto, List<MultipartFile> multipartFilelist, Member member) {
        Post post = postQuery.findPostById(id);
        if (member.getId().equals(post.getMember().getId())) {
            post.update(postRequestDto);
        } else {
            throw new CustomException(StatusCode.NO_AUTH_MEMBER);
        }

        post.update(postRequestDto);

        if (multipartFilelist != null) {
            List<ImageFile> imageFileList = imageFileRepository.findAllByPost(post);
            for (ImageFile File : imageFileList) {
                String path = File.getPath();
                String filename = path.substring(49);
                awsS3Uploader.deleteFile(filename);
            }
            imageFileRepository.deleteAll(imageFileList);

            awsS3Uploader.upload(multipartFilelist, "static", post, member);
        }
        return new PostResponseDto(post);
    }

    // 포스트 삭제
    @Transactional
    public void deletePost(Long id, Member member) {
        Post post = postQuery.findPostById(id);
        if (post.getMember().getId().equals(member.getId())) {
            try {

                List<ImageFile> imageFileList = imageFileRepository.findAllByPost(post);
                for (ImageFile imageFile : imageFileList) {
                    String path = imageFile.getPath();
                    String filename = path.substring(49);
                    awsS3Uploader.deleteFile(filename);
                }

                imageFileRepository.deleteAllByPost(post); // 게시글에 해당하는 이미지 파일 삭제

                postCommand.deletePost(id);
            } catch (CustomException e) {
                throw new CustomException(StatusCode.FILE_DELETE_FAILED);
            }
        }
    }

    // 게시글 키워드 검색
    public PostResponseListDto searchPosts(Pageable pageable, String category, String keyword) {
        Page<Post> posts = postQuery.findByKeyword(pageable, category, keyword);

        List<PostResponseDto> postResponseDto = new ArrayList<>();
        for (Post post : posts) {
            postResponseDto.add(new PostResponseDto(post));
        }

        int totalPage = posts.getTotalPages();
        if (totalPage == 0) {
            throw new CustomException(StatusCode.KEYWORD_NOT_FOUND);
        }
        return new PostResponseListDto(totalPage, postResponseDto);
    }
}
