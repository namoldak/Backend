package com.example.namoldak.dto.ResponseDto;

import com.example.namoldak.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;                                       // 작성 시간
    private LocalDateTime modifiedAt;                                      // 수정 시간

    public PostResponseDto(Post post){
        this.id           =     post.getId();
        this.title        =     post.getTitle();
        this.content      =     post.getContent();
        this.cmtCnt       =     post.getCommentList().size();
        this.nickname     =     post.getMember().getNickname();
        this.createdAt    =     post.getCreatedAt();
        this.modifiedAt   =     post.getModifiedAt();
    }
}
