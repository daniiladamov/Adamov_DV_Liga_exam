package com.example.liga_exam.mapper;

import com.example.liga_exam.dto.request.BoxReqDto;
import com.example.liga_exam.dto.response.BoxResDto;
import com.example.liga_exam.entity.Box;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BoxMapper {
    Box toEntity(BoxReqDto boxReqDto);

    BoxResDto toResponse(Box x);
}
