package com.example.liga_exam.service.implementation;

import com.example.liga_exam.entity.User;
import com.example.liga_exam.mapper.UserMapper;
import com.example.liga_exam.repository.UserRepo;
import com.example.liga_exam.security.CustomUserDetails;
import com.example.liga_exam.security.UserAppDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;


class UserDetailsServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private UserMapper userMapper;
    private User user;
    private UserAppDto dto;
    private String userName="КлимСаныч";
    private UserDetailsService detailsService;
    public UserDetailsServiceImplTest() {
        MockitoAnnotations.openMocks(this);
        detailsService=new UserDetailsServiceImpl(userRepo, userMapper);
        user=new User();
        user.setUsername(userName);
        dto=new UserAppDto();
        dto.setUsername(userName);
    }

    @Test
    void loadUserByUsername_ExpectedBehavior() {
        Mockito.when(userRepo.findByUsername(userName)).thenReturn(Optional.ofNullable(user));
        Mockito.when(userMapper.toAppDto(user)).thenReturn(dto);
        CustomUserDetails userDetails =
                (CustomUserDetails) detailsService.loadUserByUsername(userName);
        Mockito.verify(userRepo, Mockito.times(1)).findByUsername(userName);
        Mockito.verify(userMapper,Mockito.times(1)).toAppDto(user);
        Assertions.assertEquals(userDetails.getUsername(),userName);
    }
    @Test
    void loadUserByUsername_UserNotFound_ExpectedBehavior() {
        Mockito.when(userRepo.findByUsername(userName)).thenReturn(Optional.ofNullable(null));
        Throwable throwable=Assertions.assertThrows(UsernameNotFoundException.class, ()->
                detailsService.loadUserByUsername(userName));
        Assertions.assertNotNull(throwable);
        Mockito.verify(userRepo, Mockito.times(1)).findByUsername(userName);
    }
}