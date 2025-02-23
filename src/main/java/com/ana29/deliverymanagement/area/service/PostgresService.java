package com.ana29.deliverymanagement.area.service;

import com.ana29.deliverymanagement.area.dto.GetAreaRequestDto;
import com.ana29.deliverymanagement.area.dto.GetAreaResponseDto;
import com.ana29.deliverymanagement.area.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service("postgres")
@RequiredArgsConstructor
public class PostgresService implements AreaServiceInterface {

    private final AreaRepository areaRepository;

    @Override
    public Page<GetAreaResponseDto> getArea(GetAreaRequestDto requestDto, Pageable pageable) throws IOException {
        return areaRepository.findAreas(requestDto, pageable);
    }
}
