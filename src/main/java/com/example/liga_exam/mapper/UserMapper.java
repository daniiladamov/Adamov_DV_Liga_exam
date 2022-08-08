package com.example.liga_exam.mapper;

import com.example.liga_exam.security.UserAppDto;
import com.example.liga_exam.dto.request.UserRegisterDto;
import com.example.liga_exam.dto.response.UserResDto;
import com.example.liga_exam.entity.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    UserAppDto toAppDto(User user);
    UserResDto toResponse(User user);

    User toEntity(UserRegisterDto dto);
}
