package com.example.liga_exam.service;

public interface AuthService {
    void auth(String username, String password);
    String getUsername();
}
