package com.geonlee.spring.security.provider;

import com.geonlee.spring.shared.enums.ErrorCode;
import com.geonlee.spring.security.exception.CustomJwtException;
import com.geonlee.spring.security.userDetail.UserDetailsImpl;
import com.geonlee.spring.security.userDetail.UserDetailsServiceImpl;
import com.geonlee.spring.user.domain.aggregate.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtTokenProvider {

    private static final String PROFILE_PATH = "/api/user/profile";
    private static final String ID_CLAIM_NAME = "id";
    private static final String EMAIL_CLAIM_NAME = "email";
    private static final String ROLE_CLAIM_NAME = "role";

    private final SecretKey secretKey;
    @Value("${spring.jwt.access.expiredAt}")
    private Long accessTokenExpiredTime;
    @Value("${spring.jwt.email.expiredAt}")
    private Long emailTokenExpiredTime;

    public JwtTokenProvider(
            @Value("${spring.jwt.secret}") String key
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));
    }

    public String createAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ID_CLAIM_NAME, user.getId());
        claims.put(EMAIL_CLAIM_NAME, user.getEmail());
        claims.put(ROLE_CLAIM_NAME, user.getRole());
        return buildToken(claims, accessTokenExpiredTime);
    }

    public String createEmailVerificationToken(String email) {
        return buildToken(Collections.singletonMap(EMAIL_CLAIM_NAME, email),
                emailTokenExpiredTime);
    }

    public boolean isValidateToken(String token) {
        return StringUtils.hasText(token) && !getExpirationByToken(token).before(new Date());
    }

    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get(ID_CLAIM_NAME, Long.class);
    }

    public String getEmailByToken(String token) {
        return parseClaims(token).get(EMAIL_CLAIM_NAME, String.class);
    }

    public String resolveToken(String header) {
        return Optional.ofNullable(header)
                .map(h -> h.replace("Bearer ", ""))
                .orElseThrow(() -> new CustomJwtException(ErrorCode.INVALID_AUTHENTICATION));
    }

    public Date getExpirationByToken(String token) {
        return parseClaims(token).getExpiration();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (MalformedJwtException | SignatureException ex) {
            throw new CustomJwtException(ErrorCode.MALFORMED_JWT_EXCEPTION);
        } catch (ExpiredJwtException ex) {
            throw new CustomJwtException(ErrorCode.EXPIRED_JWT_EXCEPTION);
        } catch (UnsupportedJwtException ex) {
            throw new CustomJwtException(ErrorCode.UNSUPPORTED_JWT_EXCEPTION);
        } catch (IllegalArgumentException ex) {
            throw new CustomJwtException(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION);
        }
    }

    private String buildToken(Map<String, Object> claims, long expireTime) {
        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireTime))
                .signWith(secretKey)
                .compact();
    }
}
