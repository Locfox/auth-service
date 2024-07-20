package com.locfox.filem.auth.repository;

import com.locfox.filem.auth.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Query(value = "select * from account where nickname = ?1", nativeQuery = true)
    Account getUserByNickname(String nickname);

    @Query(value = "select * from account where email = ?1", nativeQuery = true)
    Account getUserByEmail(String email);

}
