package com.example.namoldak.domainModel;

import com.example.namoldak.domain.Comment;
import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import com.example.namoldak.repository.CommentRepository;
import com.example.namoldak.repository.PostRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

// 기능 : 포스트 도메인 관련 DB Read 관리
@Service
@RequiredArgsConstructor
public class PostQuery {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    //////////////TODO 포스트 관련
    // 포스트 ID로 포스트 찾아오기
    public Post findPostById(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(StatusCode.POST_NOT_FOUND)
        );
        return post;
    }

    // 카테고리별 모든 포스트 불러오기
    public List<Post> findAllByCategory(String category) {
        List<Post> posts = postRepository.findAllByCategory(category);
        return posts;
    }

    // 유저 본인이 쓴 피드백만 불러오기
    public List<Post> findAllByMemberAndCategory(Member member, String category) {
        List<Post> posts = postRepository.findAllByMemberAndCategory(member, category);
        return posts;
    }

    // 페이징 처리한 자유게시판 불러오기
    public Page<Post> findAllByCategory(Pageable pageable, String category){
        Page<Post> postList = postRepository.findAllByCategory(pageable, category);
        return postList;
    }

    // 페이징 처리한 내가쓴피드백 불러오기
    public Page<Post> findAllByMemberAndCategoryOrderByCreatedAtDesc(Pageable pageable, Member member, String category){
        Page<Post> postList = postRepository.findAllByMemberAndCategoryOrderByCreatedAtDesc(pageable, member, category);
        return postList;
    }

    // 게시글 키워드 검색
    public Page<Post> findByKeyword(Pageable pageable, String category, String keyword) {
        Page<Post> posts = postRepository.findAllByCategoryContainingAndTitleContaining(pageable, category, keyword);
        return posts;
    }

    //////////////TODO 댓글 관련
    // 댓글 ID로 댓글 찾아오기
    public Comment findCommentById(Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(StatusCode.COMMENT_NOT_FOUND)
        );
        return comment;
    }
}
