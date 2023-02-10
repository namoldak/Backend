package com.example.namoldak.domainModel;

import com.example.namoldak.domain.ImageFile;
import com.example.namoldak.domain.Member;
import com.example.namoldak.repository.*;
import com.example.namoldak.util.s3.AwsS3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

// 기능 : 회원 도메인 관련 DB CUD 관리
@Service
@RequiredArgsConstructor
public class MemberCommand {
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ImageFileRepository imageFileRepository;
    private final GameRoomAttendeeRepository gameRoomAttendeeRepository;
    private final AwsS3Uploader awsS3Uploader;
    private final RewardReposiroty rewardReposiroty;

    // 멤버 객체로 데이터 삭제하기
    public void deleteMember(Member member) {
        memberRepository.delete(member);
    }

    // 멤버 객체 저장하기
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    // 회원탈퇴하며 모든 정보를 정리하기
    public void removeMemberInfo(Member member) {
        if(commentRepository.existsByMember(member)){
            commentRepository.deleteAllByMember(member);
        }
        // 게임룸 참여 여부 확인
        if(gameRoomAttendeeRepository.existsByMember(member)){
            gameRoomAttendeeRepository.deleteAllByMember(member);
        }
        // 이미지파일 여부 확인
        if(imageFileRepository.existsByMember(member)){

            List<ImageFile> imageFileList = imageFileRepository.findAllByMember(member);
            for (ImageFile imageFile : imageFileList) {
                String path = imageFile.getPath();
                String filename = path.substring(49);
                awsS3Uploader.deleteFile(filename);
            }

            imageFileRepository.deleteAllByMember(member);
        }
        // 글 여부 확인
        if(postRepository.existsByMember(member)){
            postRepository.deleteAllByMember(member);
        }
        // 리워드 여부 확인
        if(rewardReposiroty.existsByMember(member)){
            rewardReposiroty.deleteAllByMember(member);
        }
        // 회원 삭제
        deleteMember(member);
    }
}
