package com.example.liga_exam.mapper;

import com.example.liga_exam.dto.request.OperationReqDto;
import com.example.liga_exam.dto.response.OperationResDto;
import com.example.liga_exam.entity.Operation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ServiceMapper {
    Operation toEntity(OperationReqDto operationReqDto);
    OperationResDto toResponse(Operation operation);
}
