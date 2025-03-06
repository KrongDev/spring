package com.geonlee.spring.user.domain;

import com.geonlee.spring.security.provider.JwtTokenProvider;
import com.geonlee.spring.shared.enums.ErrorCode;
import com.geonlee.spring.shared.exception.BaseException;
import com.geonlee.spring.user.domain.aggregate.User;
import com.geonlee.spring.user.domain.aggregate.vo.UserRole;
import com.geonlee.spring.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signUp(String email, String name, String password, UserRole role) {
        if(existsByEmail(email)) {
           throw new BaseException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = User.of(name, email, encodedPassword, role);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String signIn(String email, String password) {
        User user = findByEmail(email);
        user.validatePassword(passwordEncoder, password);
        return jwtTokenProvider.createAccessToken(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
