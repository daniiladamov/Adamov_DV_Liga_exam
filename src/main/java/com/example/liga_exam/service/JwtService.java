package com.example.liga_exam.service;

import com.example.liga_exam.dto.response.JwtResDto;
import org.springframework.data.util.Pair;

public interface JwtService {
    String generateToken(String username,String secretKey, int lifeTime, String uuid);

    JwtResDto generateTokens(String username);

    Pair<String, String> validateJwtAccessToken(String jwtToken);

    String validateJwtRefreshToken(String jwtToken);
}
