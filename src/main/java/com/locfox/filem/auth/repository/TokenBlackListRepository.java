package com.locfox.filem.auth.repository;

import com.locfox.filem.auth.entity.JwtBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlackListRepository extends JpaRepository<JwtBlackList, Integer> {
    @Query(value = "select * from jwt_black_list where jwt_token = ?1", nativeQuery = true)
    JwtBlackList findToken(String token);
}
