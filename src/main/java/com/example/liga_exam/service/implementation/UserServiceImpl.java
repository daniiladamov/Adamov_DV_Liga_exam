package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.User;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.UserRepo;
import com.example.liga_exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    @Value("${exception_message}")
    private String exceptionMessage;
    private final static String INVALID_USERNAME="Пользователя с username:%s не существует";
    @Override
    public User getUser(Long id) {
       return userRepo.findById(id).orElseThrow(()->
               new EntityNotFoundException(exceptionMessage+id));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(()->
                new UsernameNotFoundException(String.format(INVALID_USERNAME,username)));
    }

}
