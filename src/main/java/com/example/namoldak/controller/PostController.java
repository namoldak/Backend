package com.example.namoldak.controller;

import com.example.namoldak.dto.RequestDto.PostRequestDto;
import com.example.namoldak.service.PostService;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.ResponseUtil;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


// 포스트 관련 CRUD 컨트롤러
@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    // 게시글 작성
    @PostMapping (value = "/posts/write", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addPost(@RequestPart(value = "data") PostRequestDto postRequestDto,
                                     @RequestPart(value = "file",required = false) List<MultipartFile> multipartFilelist,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return ResponseUtil.response(postService.addPost(postRequestDto, multipartFilelist, userDetails.getMember()));
    }

    // 게시글 전체 불러오기
    @GetMapping("/posts/all") //'/posts/all?page=1&size=10'
    public ResponseEntity<?> getAllPost(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseUtil.response(postService.getAllPost(pageable));
    }

    // 게시글 카테고리별 불러오기
    @GetMapping("/posts/category") //'posts/category?category=자유게시판&page=0&size=10'??????????
    public ResponseEntity<?> getCategoryPost(@PageableDefault(page = 0, size = 10) Pageable pageable,
                                             @RequestParam(required = false) String category) {
        return ResponseUtil.response(postService.getCategoryPost(pageable, category));
    }

    // 게시글 상세 조회
    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getOnePost(@PathVariable Long id) {
        return ResponseUtil.response(postService.getOnePost(id));
    }

    // 게시글 수정
    @PutMapping("/posts/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                        @RequestPart(value = "data") PostRequestDto postRequestDto,
                                        @RequestPart(value = "file", required = false) List<MultipartFile> multipartFilelist,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return ResponseUtil.response(postService.updatePost(id, postRequestDto, multipartFilelist, userDetails.getMember()));
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.deletePost(id, userDetails.getMember());
        return ResponseUtil.response(StatusCode.DELETE_OK);
    }
}
