package com.example.namoldak.repository;

import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import javax.transaction.Transactional;
import java.util.List;

// 기능 : 리워드 정보 레포
public interface RewardReposiroty extends JpaRepository<Reward, Long>  {
    List<Reward> findByMember(Member member);
    @Transactional
    void deleteAllByMember(Member member);
    boolean existsByMember(Member member);
}
