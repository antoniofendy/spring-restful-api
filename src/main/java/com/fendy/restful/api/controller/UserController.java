package com.fendy.restful.api.controller;

import com.fendy.restful.api.entity.User;
import com.fendy.restful.api.model.RegisterUserRequest;
import com.fendy.restful.api.model.UserResponse;
import com.fendy.restful.api.model.WebResponse;
import com.fendy.restful.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(
            path = "/api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest registerUserRequest) {
        userService.register(registerUserRequest);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/api/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> getUser(User user) {
        UserResponse userResponse = userService.getUser(user);

        return WebResponse.<UserResponse>builder()
                .data(userResponse)
                .build();
    }

}
