package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.User;
import com.example.liga_exam.exception.EntityNotFoundException;
import com.example.liga_exam.repository.UserRepo;
import com.example.liga_exam.security.RoleEnum;
import com.example.liga_exam.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.example.liga_exam.security.RoleEnum.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private PasswordEncoder encoder;
    private UserService userService;
    private User user;
    private String password="12345678";
    private String passwordEncode="87654321";
    private Long id=1L;
    private String username="ДимЮрич";

    public UserServiceImplTest() {
        MockitoAnnotations.openMocks(this);
        userService=new UserServiceImpl(encoder, userRepo);
        user=new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
    }

    @Test
    void getUser_ExpectedBehavior(){
        Mockito.when(userRepo.findById(id)).thenReturn(Optional.ofNullable(user));
        User userBd = userService.getUser(id);
        Mockito.verify(userRepo, Mockito.times(1)).findById(id);
        Assertions.assertEquals(user.getId(),userBd.getId());
    }
    @Test
    void getUser_UserNotFound_ExpectedBehavior(){
        Mockito.when(userRepo.findById(id)).thenReturn(Optional.ofNullable(null));
        Throwable throwable=Assertions.assertThrows(EntityNotFoundException.class, ()->
                userService.getUser(id));
        Mockito.verify(userRepo, Mockito.times(1)).findById(id);
        Assertions.assertNotNull(throwable);
    }

    @Test
    void getUserByUsername_ExpectedBehavior() {
        Mockito.when(userRepo.findByUsername(username)).thenReturn(Optional.ofNullable(user));
        User userBd = userService.getUserByUsername(username);
        Mockito.verify(userRepo, Mockito.times(1)).findByUsername(username);
        Assertions.assertEquals(user.getId(),userBd.getId());
    }

    @Test
    void getUserByUsername_UserNotFound_ExpectedBehavior() {
        Mockito.when(userRepo.findByUsername(username)).thenReturn(Optional.ofNullable(null));
        Throwable throwable=Assertions.assertThrows(UsernameNotFoundException.class, ()->
                userService.getUserByUsername(username));
        Mockito.verify(userRepo, Mockito.times(1)).findByUsername(username);
        Assertions.assertNotNull(throwable);
    }
    @Test
    void createUser() {
        Mockito.when(userRepo.save(user)).thenReturn(user);
        Mockito.when(encoder.encode(password)).thenReturn(passwordEncode);
        Long userId = userService.createUser(user);
        Mockito.verify(userRepo,Mockito.times(1)).save(user);
        Mockito.verify(encoder,Mockito.times(1)).encode(password);
        Assertions.assertEquals(id,userId);
        Assertions.assertEquals(ROLE_USER,user.getRole());
        Assertions.assertEquals(passwordEncode,user.getPassword());
    }

    @Test
    void removeUser() {
        Mockito.when(encoder.encode(null)).thenReturn(passwordEncode);
        Mockito.when(userRepo.save(user)).thenReturn(user);
        userService.removeUser(user);
        Mockito.verify(encoder,Mockito.times(1)).encode(null);
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
        Assertions.assertNotEquals(username,user.getUsername());
        Assertions.assertEquals(passwordEncode,user.getPassword());
        Assertions.assertEquals(REMOVED,user.getRole());
    }
}