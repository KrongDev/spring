package com.geonlee.spring.user.interfaces.dto.request;

import com.geonlee.spring.user.domain.aggregate.vo.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest (
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        @NotBlank(message = "이름을 입력해주세요.")
        String name,
        @NotBlank(message = "패스워드를 입력해주세요.")
        String password,
        UserRole role
) {
}
