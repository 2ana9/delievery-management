package com.ana29.deliverymanagement.area.service;

import com.ana29.deliverymanagement.area.dto.GetAreaRequestDto;
import com.ana29.deliverymanagement.area.dto.GetAreaResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface AreaServiceInterface {
    Page<GetAreaResponseDto> getArea(GetAreaRequestDto requestDto, Pageable pageable) throws IOException;
}
