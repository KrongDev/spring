package com.geonlee.spring.security.userDetail;

import com.geonlee.spring.shared.enums.ErrorCode;
import com.geonlee.spring.security.exception.AuthenticationException;
import com.geonlee.spring.user.domain.UserService;
import com.geonlee.spring.user.domain.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        validateUser(userDetails);

        return userDetails;
    }

    public Authentication getAuthentication(String email) {
        UserDetailsImpl userDetails = (UserDetailsImpl) loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private void validateUser(UserDetailsImpl user) {
        if (user == null) {
            throw new AuthenticationException(ErrorCode.NOT_FOUND_USER);
        }

        validateEnabled(user);
        validateCredentialNonExpired(user);
        validateAccountNonLocked(user);
        validateAccountExpired(user);
    }

    private static void validateEnabled(UserDetailsImpl user) {
        if (!user.isEnabled()) {
            throw new AuthenticationException(ErrorCode.USER_DISABLED);
        }
    }

    private static void validateCredentialNonExpired(UserDetailsImpl user) {
        if (!user.isCredentialsNonExpired()) {
            throw new AuthenticationException(ErrorCode.NON_EXPIRED_ACCOUNT);
        }
    }

    private static void validateAccountNonLocked(UserDetailsImpl user) {
        if (!user.isAccountNonLocked()) {
            throw new AuthenticationException(ErrorCode.ACCOUNT_LOCKED);
        }
    }

    private static void validateAccountExpired(UserDetailsImpl user) {
        if (!user.isAccountNonExpired()) {
            throw new AuthenticationException(ErrorCode.ACCOUNT_EXPIRED);
        }
    }
}
