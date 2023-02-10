package com.example.namoldak.repository;

import com.example.namoldak.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// 기능 : RefreshToken Redis에 저장 및 관리 레포
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    void deleteById(String email);  // email로 token 삭제
    boolean existsById(String email);   // email로 존재여부 확인
}
