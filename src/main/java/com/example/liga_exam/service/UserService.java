package com.example.liga_exam.service;

import com.example.liga_exam.entity.User;

public interface UserService {
    User getUser(Long id);

    User getUserByUsername(String username);

    String registerUser(User user);

    Long confirmUser(Long id);
    void deleteNotConfirmUser(Long id);
}
