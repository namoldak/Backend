package com.example.namoldak.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

// 기능 : S3에 저장한 이미지 정보 저장 Entity
@Getter
@Entity
@NoArgsConstructor
public class ImageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // id

    @Column(nullable = false)           // image 경로
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;                  // userid

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public ImageFile(String path, Member member, Post post) {
        this.path    =  path;
        this.member  =  member;
        this.post    =  post;
    }
}
