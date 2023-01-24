package com.example.namoldak.domain;

import com.example.namoldak.dto.RequestDto.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// 기능 : 포스트 정보 Entity
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                                          // 고유 ID
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;                                                    // Member 와 연관 관계 설정
    @Column
    private String title;                                                     // 포스트 타이틀
    @Column
    private String content;                                                   // 포스트 내용
    @Column
    private String nickname;                                                  // 작성자 닉네임
    @Column
    private String category;                                                  // 카테고리
    @Column(nullable = false) // 게시판 이미지는 0개 이상, 1개 이하로 Null 값 허용
    private String imageFile; // s3 Upload Url

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)               // 연관된 post가 삭제되면 함께 삭제되도록 cascade 설정
    private List<Comment> commentList = new ArrayList<>();                    // 댓글 리스트

    public Post(PostRequestDto postRequestDto, String imageFile, Member member) {
        this.member = member;
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
        this.nickname = member.getNickname();
        this.category = postRequestDto.getCategory();
        this.imageFile = imageFile;
    }

    public void update(PostRequestDto postRequestDto) {
        this.title     = postRequestDto.getTitle();
        this.content   = postRequestDto.getContent();
    }

    public void update(String imageFile) {
        this.imageFile = imageFile;
    }
}
