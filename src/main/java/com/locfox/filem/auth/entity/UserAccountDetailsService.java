package com.locfox.filem.auth.entity;

import com.locfox.filem.auth.repo.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Loads users from database.
 */
public class UserAccountDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account account = repository.getUserByNickname(username);

        if (account == null) {
            throw new UsernameNotFoundException("User " + username + "is not found");
        }

        return User.builder()
                .username(account.getNickname())
                .password(account.getPasswordHash())
                .build();
    }

}
