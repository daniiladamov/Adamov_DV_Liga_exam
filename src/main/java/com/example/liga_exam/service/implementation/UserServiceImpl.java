package com.example.liga_exam.service.implementation;

import com.example.liga_exam.security.RoleEnum;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.exception.UserConfirmException;
import com.example.liga_exam.repository.UserRepo;
import com.example.liga_exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    @Value("${exception_message}")
    private String exceptionMessage;
    @Value("${time_period}")
    private Long timeForConfirm;
    private final static String REPEAT_CONFIRM="Пользователь уже подтведил свою регистрацию";
    private final static String INVALID_USERNAME = "Пользователя с username:%s не существует";

    @Override
    @PostAuthorize("hasRole('ADMIN') || (returnObject.username.equals(authentication.name))")
    public User getUser(Long id) {
        return userRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException(exceptionMessage + id));
    }

    @Override
    @PostAuthorize("hasAnyRole('ADMIN','EMPLOYEE') || returnObject.username.equals(authentication.name)")
    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format(INVALID_USERNAME, username)));
    }

    @Override
    @Transactional
    public Long confirmUser(Long id) {
        User user = getUser(id);
        if (Objects.isNull(user.getRole())) {
            user.setRole(RoleEnum.ROLE_USER);
            return userRepo.save(user).getId();
        }
        else throw new UserConfirmException(REPEAT_CONFIRM);
    }

    @Override
    @Transactional
    public String registerUser(User user) {
        user.setPassword(passwordEncoder.encode(
                user.getPassword()
        ));
        User saveUser = userRepo.save(user);
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.MINUTES.sleep(timeForConfirm);
                deleteNotConfirmUser(saveUser.getId());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        return "http://localhost:8080/api/auth/confirm/" + saveUser.getId();
    }

    @Override
    @Transactional
    public void deleteNotConfirmUser(Long id) {
        User user = getUser(id);
        if (Objects.isNull(user.getRole()))
            userRepo.delete(user);
    }
}
