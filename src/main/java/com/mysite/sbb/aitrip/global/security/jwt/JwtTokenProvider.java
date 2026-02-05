package com.mysite.sbb.aitrip.global.security.jwt;

import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(JwtProperties jwtProperties, UserDetailsService userDetailsService) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                Base64.getEncoder().encodeToString(jwtProperties.secret().getBytes())
        ));
        this.accessTokenExpiration = jwtProperties.accessTokenExpiration();
        this.refreshTokenExpiration = jwtProperties.refreshTokenExpiration();
        this.userDetailsService = userDetailsService;
    }

    // Access Token 생성
    public String createAccessToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String email = claims.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // 토큰의 남은 만료 시간 (밀리초)
    public long getRemainingExpiration(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
            return false;
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰입니다.");
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있습니다.");
            return false;
        }
    }

    // Refresh Token 검증 (예외 발생)
    public void validateRefreshToken(String token) {
        try {
            parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
