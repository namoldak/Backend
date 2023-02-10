package com.example.namoldak.domain;

import com.example.namoldak.dto.RequestDto.CommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// 기능 : 댓글 정보 Entity
@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 대댓글
    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Comment> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public Comment(CommentRequestDto commentRequestDto, Member member, Post post) {
        this.nickname = member.getNickname();
        this.comment = commentRequestDto.getComment();
        this.member = member;
        this.post = post;
    }

    public Comment(CommentRequestDto commentRequestDto, Member member, Post post, Comment parent) {
        this.nickname = member.getNickname();
        this.comment = commentRequestDto.getComment();
        this.parent = parent;
        this.member = member;
        this.post = post;
    }

    public void update(CommentRequestDto commentRequestDto) {
        this.comment = commentRequestDto.getComment();
    }
}
