package com.example.liga_exam.mapper;

import com.example.liga_exam.dto.request.OrderReqDto;
import com.example.liga_exam.dto.response.OrderResDto;
import com.example.liga_exam.entity.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ServiceMapper.class, BoxMapper.class, UserMapper.class})
public interface OrderMapper {
    Order toEntity(OrderReqDto orderReqDto);
    OrderResDto toResponse(Order order);
}
