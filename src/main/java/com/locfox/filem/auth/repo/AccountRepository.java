package com.locfox.filem.auth.repo;

import com.locfox.filem.auth.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Query(value = "select * from account where nickname = ?1", nativeQuery = true)
    Account getUserByNickname(String nickname);

}
