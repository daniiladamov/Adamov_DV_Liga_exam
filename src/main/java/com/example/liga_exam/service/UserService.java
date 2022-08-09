package com.example.liga_exam.service;

import com.example.liga_exam.entity.User;

public interface UserService {
    User getUser(Long id);

    User getUserByUsername(String username);

    Long createUser(User user);
}
