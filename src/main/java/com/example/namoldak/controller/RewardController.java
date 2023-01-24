package com.example.namoldak.controller;

import com.example.namoldak.service.RewardService;
import com.example.namoldak.util.GlobalResponse.ResponseUtil;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RewardController {
    private final RewardService rewardService;

    @GetMapping("/rewards")
    public ResponseEntity<?> allReward(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtil.response(rewardService.allRewardList(userDetails.getMember()));
    }
}
