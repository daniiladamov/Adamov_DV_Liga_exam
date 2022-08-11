package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.User;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.UserRepo;
import com.example.liga_exam.security.RoleEnum;
import com.example.liga_exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.liga_exam.security.RoleEnum.REMOVED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    @Value("${exception_message}")
    private String exceptionMessage;
    @Value("${default_password}")
    private String defaultPassword;
    private final static String INVALID_USERNAME = "Пользователя с username:%s не существует";

    @Override
    @PostAuthorize("hasRole('ADMIN') || " +
            "returnObject.username.equals(authentication.name)")
    public User getUser(Long id) {
        return userRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException(exceptionMessage + id));
    }

    @Override
    @PostAuthorize("hasAnyRole('ADMIN','EMPLOYEE') || " +
            "returnObject.username.equals(authentication.name)")
    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format(INVALID_USERNAME, username)));
    }

    @Override
    @Transactional
    public Long createUser(User user) {
        user.setPassword(passwordEncoder.encode(
                user.getPassword()
        ));
        user.setRole(RoleEnum.ROLE_USER);
        return userRepo.save(user).getId();
    }

    @Override
    @Transactional
    public void removeUser(User user) {
        user.setRole(REMOVED);
        user.setFirstName("removed");
        user.setLastName("removed");
        user.setSurname(null);
        user.setUsername(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(defaultPassword));
        userRepo.save(user);
    }
}
