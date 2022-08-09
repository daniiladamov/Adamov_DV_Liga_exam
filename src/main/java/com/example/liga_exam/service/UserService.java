package com.example.liga_exam.service;

import com.example.liga_exam.entity.Employee;
import com.example.liga_exam.entity.User;

import java.util.Set;

public interface UserService {
    User getUser(Long id);

    User getUserByUsername(String username);

    Long createUser(User user);

    void removeUser(User user);
}
