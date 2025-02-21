package com.ana29.deliverymanagement.area.repository;

import com.ana29.deliverymanagement.area.dto.GetAreaRequestDto;
import com.ana29.deliverymanagement.area.dto.GetAreaResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AreaRepositoryCustom {
    Page<GetAreaResponseDto> findAreas(GetAreaRequestDto condition,
                                       Pageable pageable);
}
