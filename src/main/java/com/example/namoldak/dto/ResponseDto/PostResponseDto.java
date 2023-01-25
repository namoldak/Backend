package com.example.namoldak.dto.ResponseDto;

import com.example.namoldak.domain.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 기능 : 포스트 관련 반환값을 닮을 Dto
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;                                                       // 고유 ID
    private String title;                                                  // 게시글 타이틀
    private String content;                                                // 게시글 내용
    private String nickname;                                               // 작성자 닉네임
    private int cmtCnt;                                                    // 댓글 갯수
    private String category;                                               // 카테고리
    private String imageFile;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;                                     // 작성 시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;                                    // 수정 시간
    private List<CommentResponseDto> commentList = new ArrayList<>();

    public PostResponseDto(Post post, String imageFile) { // 게시글 생성
        this.id           =     post.getId();
        this.title        =     post.getTitle();
        this.content      =     post.getContent();
        this.cmtCnt       =     post.getCommentList().size();
        this.nickname     =     post.getMember().getNickname();
        this.category     =     post.getCategory();
        this.createdAt    =     post.getCreatedAt();
        this.modifiedAt   =     post.getModifiedAt();
        this.imageFile    =     imageFile;
    }

    public PostResponseDto(Post post) { // 게시글 전체 조회, 카테고리별 조회
        this.id           =     post.getId();
        this.title        =     post.getTitle();
        this.content      =     post.getContent();
        this.cmtCnt       =     post.getCommentList().size();
        this.nickname     =     post.getMember().getNickname();
        this.category     =     post.getCategory();
        this.createdAt    =     post.getCreatedAt();
        this.modifiedAt   =     post.getModifiedAt();
    }

    public PostResponseDto(Post post, String imageFile, List<CommentResponseDto> commentList) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.cmtCnt = post.getCommentList().size();
        this.nickname = post.getNickname();
        this.category = post.getCategory();
        this.createdAt    =     post.getCreatedAt();
        this.modifiedAt   =     post.getModifiedAt();
        this.commentList = commentList;
        this.imageFile = imageFile;
    }
}
