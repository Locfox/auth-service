package com.locfox.filem.auth.controller;

import com.locfox.filem.auth.dto.EmailAndPasswordDTO;
import com.locfox.filem.auth.entity.Account;
import com.locfox.filem.auth.entity.JwtBlackList;
import com.locfox.filem.auth.entity.PasswordEmailNicknameDTO;
import com.locfox.filem.auth.repository.AccountRepository;
import com.locfox.filem.auth.repository.TokenBlackListRepository;
import com.locfox.filem.auth.service.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * AuthController
 */
@RestController
@CrossOrigin
@RequestMapping()
public class AuthController {

    private final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TokenBlackListRepository tokenRepository;

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody EmailAndPasswordDTO userAndPasswordDTO) {

        LOG.info("New login request with {}", userAndPasswordDTO);

        Account account = accountRepository.getUserByEmail(userAndPasswordDTO.email());

        if (account == null) {
            LOG.info("Account is not found: {}", userAndPasswordDTO);

            var problems = ProblemDetail.forStatus(404);
            problems.setTitle("User not found");
            problems.setDetail("User is not in database");

            return ResponseEntity.of(problems).build();
        }

        if (!passwordEncoder.matches(userAndPasswordDTO.password(), account.getPasswordHash())) {
            LOG.info("Password is not correct: {} | {}", userAndPasswordDTO, account);

            var problems = ProblemDetail.forStatus(404);
            problems.setTitle("User not found");
            problems.setDetail("User is not in database");

            return ResponseEntity.of(problems).build();
        }

        String token;
        do {
            token = jwtUtils.generateJwtToken(account.getNickname());
        } while (jwtUtils.isInBlackList(token));

        return ResponseEntity.ok(token);
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody PasswordEmailNicknameDTO passwordEmailNicknameDTO) {

        LOG.info("New register request: {}", passwordEmailNicknameDTO);

        var email = passwordEmailNicknameDTO.email().trim();

        if (!Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(email).matches()) {
            LOG.info("Email is invalid: {}", passwordEmailNicknameDTO);

            var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            problemDetail.setTitle("Invalid email");
            problemDetail.setDetail("Check if email is correct");

            return ResponseEntity.of(problemDetail).build();
        }

        var account = Account.builder()
                .nickname(passwordEmailNicknameDTO.nickname().trim().toLowerCase())
                .password(this.passwordEncoder.encode(passwordEmailNicknameDTO.password().trim()))
                .email(email)
                .build();

        try {
            this.accountRepository.save(account);
        } catch (Exception e) {
            var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            problemDetail.setTitle("Account is created");
            problemDetail.setDetail("Try to login");

            return ResponseEntity.of(problemDetail).build();
        }

        LOG.info("Saved new account: {}", account);

        return ResponseEntity.ok().build();
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        LOG.info("Logout request: {}", request);

        if (request.getHeader("Authorization") == null) {
            LOG.info("Header Authorization is null: {}", request);

            var problems = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
            problems.setTitle("No authorization header");
            problems.setDetail("Add Bearer token to a header");

            return ResponseEntity.of(problems).build();
        }

        var token = request.getHeader("Authorization").split(" ")[1];

        if (!jwtUtils.verifyToken(token)) {
            LOG.info("Token is invalid: {}", request);

            var problems = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
            problems.setTitle("Token is not valid");
            problems.setDetail("Login to get a token");

            return ResponseEntity.of(problems).build();
        }

        var jwtBlackList = new JwtBlackList(token);

        if (this.tokenRepository.findToken(token) == null) {
            this.tokenRepository.save(jwtBlackList);
        }

        LOG.info("New token added to token black list: {}", token);

        return ResponseEntity.ok().build();
    }

    @GetMapping("check_login")
    public ResponseEntity<Boolean> checkLogin(@RequestParam String token) {

        LOG.info("New check login request: {}", token);

        if (token == null) {
            LOG.info("Token is null");

            return ResponseEntity.ok(false);
        }

        var isValid = tokenRepository.findToken(token) == null &&
                jwtUtils.verifyToken(token);

        LOG.info("{} is valid: {}", token, isValid);

        return ResponseEntity.ok(isValid);
    }

    @GetMapping("openapi")
    public void getOpenApi(HttpServletResponse response) {

        response.setContentType("text/yaml");

        byte[] buf = new byte[1024];
        int bytesRead;
        int totalRead = 0;

        try (var fileInputStream = getClass().getClassLoader().getResourceAsStream("static/openapi.yml")) {

            while ((bytesRead = fileInputStream.read(buf)) > 0) {
                totalRead = totalRead + bytesRead;
                response.getOutputStream().write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            LOG.error("Open api ioexception", e);
        }
    }

}
