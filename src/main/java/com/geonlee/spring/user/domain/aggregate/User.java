package com.geonlee.spring.user.domain.aggregate;

import com.geonlee.spring.shared.enums.ErrorCode;
import com.geonlee.spring.shared.exception.BaseException;
import com.geonlee.spring.user.domain.aggregate.vo.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "`user`")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private UserRole role;
    @Column(nullable = false)
    private boolean enabled;

    public static User of(String name, String email, String password, UserRole role) {
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .role(role)
                .enabled(true)
                .build();
    }

    public void validatePassword(PasswordEncoder passwordEncoder, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, this.password)) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
