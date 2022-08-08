package com.example.liga_exam.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.liga_exam.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String JWT_PREFIX="Bearer ";
    private final UserDetailsService detailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (Objects.nonNull(authorization) && authorization.startsWith(JWT_PREFIX)){
            String jwt=authorization.replace(JWT_PREFIX,"");
            if (jwt.isBlank()){
                throw new JWTVerificationException("верификация jwt-токена не пройдена");
            }
            else {
                Pair<String, String> jwtInfo = jwtService.validateJwtAccessToken(jwt);
                CustomUserDetails userDetails = (CustomUserDetails) detailsService.loadUserByUsername(jwtInfo.getFirst());
                if (jwtInfo.getSecond().equals(userDetails.getUser().getUuid())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails,
                                    userDetails.getPassword(),
                                    userDetails.getAuthorities());
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}
