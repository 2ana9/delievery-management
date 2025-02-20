package com.ana29.deliverymanagement.area.service;

import com.ana29.deliverymanagement.area.dto.AreaRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("postgres")
@RequiredArgsConstructor
public class PostgresService implements AreaServiceInterface{

    @Override
    public Map<String, Object> searchArea(AreaRequestDto requestDto, Pageable pageable) {
        return Map.of();
    }
}
