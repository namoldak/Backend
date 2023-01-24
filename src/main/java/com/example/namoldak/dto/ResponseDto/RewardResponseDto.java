package com.example.namoldak.dto.ResponseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class RewardResponseDto {
    List<String> rewardList = new ArrayList<>();

    public void setRewardList(String reward){
        this.rewardList.add(reward);
    }
}
