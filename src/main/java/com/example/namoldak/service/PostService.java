package com.example.namoldak.service;

import com.example.namoldak.domain.*;
import com.example.namoldak.dto.RequestDto.PostRequestDto;
import com.example.namoldak.dto.ResponseDto.*;
import com.example.namoldak.repository.ImageFileRepository;
import com.example.namoldak.repository.PostRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.GlobalResponseDto;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.s3.AwsS3Service;
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
    private final AwsS3Service awsS3Service;
    private final ImageFileRepository imageFileRepository;
    private final RepositoryService repositoryService;


    // 포스트 생성
    @Transactional
    public PostResponseDto addPost(PostRequestDto postRequestDto, List<MultipartFile> multipartFilelist, Member member) throws IOException {
        Post post = new Post(postRequestDto, member);
        postRepository.save(post);

        if (multipartFilelist != null) {
            awsS3Service.upload(multipartFilelist, "static", post, member);
        }

        List<ImageFile> imageFiles = imageFileRepository.findAllByPost(post);
        List<String> imagePath = new ArrayList();
        for (ImageFile imageFile : imageFiles) {
            imagePath.add(imageFile.getPath());
        }
        PostResponseDto postResponseDto = new PostResponseDto(post, imagePath);

        return postResponseDto;
    }

    // 포스트 전체 조회
    @Transactional
    public PostResponseListDto getAllPost(Pageable pageable) {
        Page<Post> postList = repositoryService.findAllPostByPageable(pageable);
        List<Post> posts = postRepository.findAll();

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for (Post post : postList) {
            postResponseDtoList.add(new PostResponseDto(post));
        }
        int totalPage = postList.getTotalPages();
        int postCnt = posts.size();

        return new PostResponseListDto(totalPage, postCnt, postResponseDtoList);
    }

    // 카테고리별 포스트 조회
    public PostResponseListDto getCategoryPost(Pageable pageable, String category) {
        Page<Post> postList = repositoryService.findAllPostByPageableAndCategory(pageable, category);
        List<Post> posts = postRepository.findAllByCategory(category);  //FIXME

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
        List<PostResponseDto> result = new ArrayList<>();
        Post post = repositoryService.findPostById(id);

        List<String> imageFileList = new ArrayList<>();
        for (ImageFile imageFile : post.getImageFileList()) {
            imageFileList.add(imageFile.getPath());
        }

//        List<Comment> comments = repositoryService.findAllCommentByPost(post);
//        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
//        for (Comment comment : comments) {
//            commentResponseDtoList.add(new CommentResponseDto(comment));
//        }

        result.add(new PostResponseDto(post, imageFileList));
//        result.add(new PostResponseDto(post, imageFileList, commentResponseDtoList));
        return result;
    }

    // 포스트 수정
    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto postRequestDto, List<MultipartFile> multipartFilelist, Member member) throws IOException {
        Post post = repositoryService.findPostById(id);
        if (member.getId().equals(post.getMember().getId())) {
            post.update(postRequestDto);
        } else {
            throw new CustomException(StatusCode.NO_AUTH_MEMBER);
        }

        try {
            post.update(postRequestDto);

            if (multipartFilelist != null) {
                List<ImageFile> imageFileList = imageFileRepository.findAllByPost(post);
                for (ImageFile File : imageFileList) {
                    String path = File.getPath();
                    String filename = path.substring(49);
                    awsS3Service.deleteFile(filename);
                }
                imageFileRepository.deleteAll(imageFileList);

                awsS3Service.upload(multipartFilelist, "static", post, member);
            }
        } catch (IOException e) {
            throw new CustomException(StatusCode.FILE_UPLOAD_FAILED);
        }

        return new PostResponseDto(post);
    }

    // 포스트 삭제
    @Transactional
    public void deletePost(Long id, Member member) {
        Post post = repositoryService.findPostById(id);
        if (post.getMember().getId().equals(member.getId())) {
            try {

                List<ImageFile> imageFileList = imageFileRepository.findAllByPost(post);
                for (ImageFile imageFile : imageFileList) {
                    String path = imageFile.getPath();
                    String filename = path.substring(49);
                    awsS3Service.deleteFile(filename);
                }

                imageFileRepository.deleteAllByPost(post); // 게시글에 해당하는 이미지 파일 삭제

                postRepository.deleteById(id);
            } catch (CustomException e) {
                throw new CustomException(StatusCode.FILE_DELETE_FAILED);
            }
        }
    }
}
