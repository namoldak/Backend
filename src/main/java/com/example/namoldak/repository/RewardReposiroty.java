package com.example.namoldak.repository;

import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import com.example.namoldak.domain.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface RewardReposiroty extends JpaRepository<Reward, Long>  {
    List<Reward> findByMember(Member member);
    @Transactional
    void deleteAllByMember(Member member);
    boolean existsByMember(Member member);
}
