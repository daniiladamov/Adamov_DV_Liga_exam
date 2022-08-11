package com.example.liga_exam.service.implementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.liga_exam.dto.response.JwtResDto;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.repository.UserRepo;
import com.example.liga_exam.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final UserRepo userRepo;
    @Value("${jwt.secret_access_key}")
    private String secretAccessKey;
    @Value("${jwt.secret_refresh_key}")
    private String secretRefreshKey;
    @Value("${jwt.subject}")
    private String subject;
    @Value("${jwt.life_time_access}")
    private int lifeTimeAccess;
    @Value("${jwt.life_time_refresh}")
    private int lifeTimeRefresh;
    @Value("${api_name}")
    private String apiName;

    @Override
    @Transactional
    public String generateToken(String username, String secretKey, int lifeTime, String uuid) {
        User user = userRepo.findByUsername(username).orElseThrow(()->
                new UsernameNotFoundException("Пользователь не найден"));
        user.setUuid(uuid);
        userRepo.save(user);
        Date tokenLifeCycle=
                Date.from(ZonedDateTime.now().plusMinutes(lifeTime).toInstant());
        return JWT.create()
                .withSubject(subject)
                .withClaim("username",username)
                .withClaim("uuid",uuid)
                .withIssuedAt(new Date())
                .withIssuer(apiName)
                .withExpiresAt(tokenLifeCycle)
                .sign(Algorithm.HMAC256(secretKey));
    }

    @Override
    public JwtResDto generateTokens(String username) {
        String uuid= UUID.randomUUID().toString();
        String accessToken=generateToken(username,secretAccessKey, lifeTimeAccess,uuid);
        String refreshToken=generateToken(username,secretRefreshKey, lifeTimeRefresh,uuid);
        return new JwtResDto(accessToken,refreshToken);
    }

    @Override
    public Pair<String, String> validateJwtAccessToken(String jwtToken) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretAccessKey))
                .withSubject(subject)
                .withIssuer(apiName)
                .build();
        DecodedJWT decodedJWT = verifier.verify(jwtToken);
        String uuid = decodedJWT.getClaim("uuid").asString();
        String username = decodedJWT.getClaim("username").asString();
        return Pair.of(username,uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public String validateJwtRefreshToken(String jwtToken) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretRefreshKey))
                .withSubject(subject)
                .withIssuer(apiName)
                .build();
        DecodedJWT decodedJWT = verifier.verify(jwtToken);
        String username = decodedJWT.getClaim("username").asString();
        String jwtUuid = decodedJWT.getClaim("uuid").asString();
        User user = userRepo.findByUsername(username).orElseThrow(()->
                new JWTVerificationException(""));
        String uuid=user.getUuid();
        if (uuid.equals(jwtUuid))
            return username;
        else
            throw new JWTVerificationException("");
    }
}
