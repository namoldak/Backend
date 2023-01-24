package com.example.namoldak.repository;

import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import com.example.namoldak.domain.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardReposiroty extends JpaRepository<Reward, Long>  {
    List<Reward> findByMember(Member member);
}
