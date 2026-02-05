package com.mysite.sbb.aitrip.auth.service;

import com.mysite.sbb.aitrip.auth.dto.*;
import com.mysite.sbb.aitrip.global.exception.BusinessException;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.global.security.jwt.JwtTokenProvider;
import com.mysite.sbb.aitrip.user.domain.User;
import com.mysite.sbb.aitrip.user.dto.UserResponse;
import com.mysite.sbb.aitrip.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    // 회원가입
    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = request.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    // 로그인
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        tokenService.saveRefreshToken(user.getEmail(), refreshToken,
                jwtTokenProvider.getRefreshTokenExpiration());

        return new TokenResponse(accessToken, refreshToken);
    }

    // 토큰 갱신
    public TokenResponse refresh(RefreshRequest request) {
        jwtTokenProvider.validateRefreshToken(request.refreshToken());

        String email = jwtTokenProvider.getEmailFromToken(request.refreshToken());
        String storedToken = tokenService.getRefreshToken(email);

        if (storedToken == null || !storedToken.equals(request.refreshToken())) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(email);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

        tokenService.saveRefreshToken(email, newRefreshToken,
                jwtTokenProvider.getRefreshTokenExpiration());

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    // 로그아웃
    public void logout(String accessToken, String email) {
        long remainingExpiration = jwtTokenProvider.getRemainingExpiration(accessToken);
        tokenService.addToBlacklist(accessToken, remainingExpiration);
        tokenService.deleteRefreshToken(email);
    }
}
