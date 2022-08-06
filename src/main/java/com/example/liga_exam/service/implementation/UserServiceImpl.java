package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.User;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.UserRepo;
import com.example.liga_exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    @Value("${exception_message}")
    private String exceptionMessage;
    @Override
    public User getUser(Long id) {
       return userRepo.findById(id).orElseThrow(()->
               new EntityNotFoundException(exceptionMessage+id));
    }
}
