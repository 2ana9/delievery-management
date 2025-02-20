package com.ana29.deliverymanagement.area.service;

import com.ana29.deliverymanagement.area.dto.AreaRequestDto;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Map;

public interface AreaServiceInterface {
    Map<String, Object> searchArea(AreaRequestDto requestDto, Pageable pageable) throws IOException;
}
