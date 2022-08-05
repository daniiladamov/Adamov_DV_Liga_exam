package com.example.liga_exam.mapper;

import com.example.liga_exam.dto.UserAppDto;
import com.example.liga_exam.entity.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    UserAppDto toAppDto(User user);
}
