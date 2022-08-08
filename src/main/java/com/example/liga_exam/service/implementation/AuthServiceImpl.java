package com.example.liga_exam.service.implementation;

import com.example.liga_exam.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    @Override
    public void auth(String username, String password) {
        UsernamePasswordAuthenticationToken authToken=
                new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authToken);
    }

    @Override
    public String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
