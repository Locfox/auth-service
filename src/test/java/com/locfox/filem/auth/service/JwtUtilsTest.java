package com.locfox.filem.auth.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    private Authentication authentication = new Authentication() {

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of();
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return new UserDetails() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return List.of();
                }

                @Override
                public String getPassword() {
                    return "aboba";
                }

                @Override
                public String getUsername() {
                    return "aboba";
                }
            };
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

        @Override
        public String getName() {
            return "";
        }
    };

    @Test
    void verifyTest() {

        var token = jwtUtils.generateJwtToken(authentication);


        assertTrue(jwtUtils.verifyToken(token));
        assertFalse(jwtUtils.verifyToken(token+"D"));

        assertFalse(jwtUtils.verifyToken(token+"aboba"));
        assertFalse(jwtUtils.verifyToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"));
    }

    @Test
    public void decodeNicknameToken() {
        var token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYm9iYSIsImlhdCI6MTcyMTA3NDMwN30.55JhnrWxHJqfCQfl2ONbSSUY_W5tCMnA11kHpWcC_lA";

        var nickname = this.jwtUtils.getNicknameFromToken(token);
        assertEquals(nickname, "aboba");
    }

}