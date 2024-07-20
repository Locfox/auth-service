package com.locfox.filem.auth.service;

import com.locfox.filem.auth.repository.TokenBlackListRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Service
public class JwtUtils {

    @Autowired
    private TokenBlackListRepository tokenRepository;

    private String jwtSecret = "aboba243dsKLKDbklds890257kjKLSDJFKLJsldkfjKLJFADKL2h3n4BNewf";

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate and return valid jwt token
     *
     * @param authentication important is nickname from {@link UserDetails} in {@link Authentication#getPrincipal()}
     * @return valid jwt token
     */
    public String generateJwtToken(Authentication authentication) {

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .subject((userPrincipal.getUsername()))
                .issuedAt(new Date())
                .signWith(this.getSigningKey())
                .compact();
    }

    /**
     * Generate and return valid jwt token
     *
     * @param nickname user nickname
     * @return valid jwt token
     */
    public String generateJwtToken(String nickname) {

        return Jwts.builder()
                .subject(nickname)
                .issuedAt(new Date())
                .signWith(this.getSigningKey())
                .compact();
    }

    /**
     * Checks, if token is valid
     *
     * @param token jwt token
     * @return true if token is valid otherwise false
     */
    public boolean verifyToken(String token) {

        JwtParser jwtParser = Jwts.parser()
                .verifyWith((SecretKey) this.getSigningKey())
                .build();

        try {
            jwtParser.parse(token);
        } catch (JwtException e) {
            return false;
        }

        return true;
    }

    /**
     * Parse nickname from jwt token
     * <p>
     * This method do not validate a token
     *
     * @param token jwt token
     * @return nickname from 2 part of token
     */
    public String getNicknameFromToken(String token) {

        return new JSONObject(new String(Base64.getDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8)).getString("sub");
    }

    public boolean isInBlackList(String token) {

        return this.tokenRepository.findToken(token) != null;
    }

}
