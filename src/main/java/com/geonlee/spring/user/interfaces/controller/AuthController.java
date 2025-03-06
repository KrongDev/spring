package com.geonlee.spring.user.interfaces.controller;

import com.geonlee.spring.shared.enums.SuccessCode;
import com.geonlee.spring.shared.response.CommonResponse;
import com.geonlee.spring.shared.response.VoidResponse;
import com.geonlee.spring.user.domain.UserService;
import com.geonlee.spring.user.domain.aggregate.User;
import com.geonlee.spring.user.interfaces.dto.request.SignInRequest;
import com.geonlee.spring.user.interfaces.dto.request.SignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    //
    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<CommonResponse<VoidResponse>> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        //
        userService.signUp(
                signUpRequest.email(),
                signUpRequest.name(),
                signUpRequest.password(),
                signUpRequest.role()
        );
        return CommonResponse.success(SuccessCode.SUCCESS_INSERT);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<CommonResponse<String>> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        String accessToken = userService.signIn(signInRequest.email(), signInRequest.password());
        return CommonResponse.success(SuccessCode.SUCCESS, accessToken);
    }
}
