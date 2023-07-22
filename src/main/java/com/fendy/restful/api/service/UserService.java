package com.fendy.restful.api.service;

import com.fendy.restful.api.entity.User;
import com.fendy.restful.api.model.RegisterUserRequest;
import com.fendy.restful.api.repository.UserRepository;
import com.fendy.restful.api.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    public void register(RegisterUserRequest registerUserRequest) {

        // validation request
        validationService.validate(registerUserRequest);

        // check if username existed
        if (userRepository.existsById(registerUserRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        User user = new User();
        user.setUsername(registerUserRequest.getUsername());
        user.setPassword(BCrypt.hashpw(registerUserRequest.getPassword(), BCrypt.gensalt()));
        user.setName(registerUserRequest.getName());

        userRepository.save(user);

    }

}
