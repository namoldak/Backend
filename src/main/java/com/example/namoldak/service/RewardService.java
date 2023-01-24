package com.example.namoldak.service;

import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Reward;
import com.example.namoldak.dto.ResponseDto.PrivateResponseBody;
import com.example.namoldak.dto.ResponseDto.RewardResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardService {
    private final RepositoryService repositoryService;

    @Transactional
    public boolean createWinReward(Member member) {
        if (member.getWinNum() == 1) {
            Reward reward = new Reward("병아리", member);
            repositoryService.saveReward(reward);
            return true;
        } else if (member.getWinNum() == 40) {
            Reward reward = new Reward("닭", member);
            repositoryService.saveReward(reward);
            return true;
        } else if (member.getWinNum() == 60) {
            Reward reward = new Reward("봉황", member);
            repositoryService.saveReward(reward);
            return true;
        } else {
            return false;
        }
    }

    public boolean createLoseReward(Member member) {
        if (member.getLoseNum() == 1) {
            Reward reward = new Reward("병든 병아리", member);
            repositoryService.saveReward(reward);
            return true;
        } else if (member.getLoseNum() == 40) {
            Reward reward = new Reward("병든 닭", member);
            repositoryService.saveReward(reward);
            return true;
        } else if (member.getLoseNum() == 60) {
            Reward reward = new Reward("그는 좋은 닭이었습니다...", member);
            repositoryService.saveReward(reward);
            return true;
        } else {
            return false;
        }
    }

    public boolean createTotalGameReward(Member member) {
        if (member.getTotalGameNum() == 1) {
            Reward reward = new Reward("게임에 중독된 병아리", member);
            repositoryService.saveReward(reward);
            return true;
        } else if (member.getTotalGameNum() == 40) {
            Reward reward = new Reward("게임에 중독된 닭", member);
            repositoryService.saveReward(reward);
            return true;
        } else if (member.getTotalGameNum() == 60) {
            Reward reward = new Reward("이제 그만 인생을 살아가세요 휴먼...", member);
            repositoryService.saveReward(reward);
            return true;
        } else {
            return false;
        }
    }

    public RewardResponseDto allRewardList(Member member) {
        List<Reward> rewardList = repositoryService.findAllReward(member);
        RewardResponseDto rewardResponseDto = new RewardResponseDto();
        for (Reward reward : rewardList) {
            rewardResponseDto.setRewardList(reward.getRewardName());
        }
        return rewardResponseDto;
    }
}
