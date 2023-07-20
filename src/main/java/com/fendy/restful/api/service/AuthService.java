package com.fendy.restful.api.service;

import com.fendy.restful.api.entity.User;
import com.fendy.restful.api.model.LoginUserRequest;
import com.fendy.restful.api.model.TokenResponse;
import com.fendy.restful.api.repository.UserRepository;
import com.fendy.restful.api.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    public TokenResponse login(LoginUserRequest loginUserRequest) {
        validationService.validate(loginUserRequest);

        User user = userRepository.findById(loginUserRequest.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Username or password wrong"
                ));

        if(BCrypt.checkpw(loginUserRequest.getPassword(), user.getPassword())) {
            // login successfully
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(System.currentTimeMillis() + nextThirtyDays());
            userRepository.save(user);

            return TokenResponse.builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong");
        }
    }

    private Long nextThirtyDays() {
        return System.currentTimeMillis() + (1000 * 60 * 24 * 30);
    }

}
