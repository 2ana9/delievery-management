package com.ana29.deliverymanagement.area.controller;

import com.ana29.deliverymanagement.area.dto.AreaRequestDto;
import com.ana29.deliverymanagement.area.dto.GetAreaRequestDto;
import com.ana29.deliverymanagement.area.dto.GetAreaResponseDto;
import com.ana29.deliverymanagement.area.service.AreaServiceFactory;
import com.ana29.deliverymanagement.area.service.AreaServiceInterface;
import com.ana29.deliverymanagement.area.service.ElasticSearchService;
import com.ana29.deliverymanagement.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/area")
@RequiredArgsConstructor
public class AreaController {

    private final ElasticSearchService elasticSearchService;
    private final AreaServiceFactory areaServiceFactory;

    @GetMapping("/search")
    public ResponseEntity<ResponseDto<Page<GetAreaResponseDto>>> getAreas(@ModelAttribute GetAreaRequestDto requestDto, Pageable pageable) throws IOException {
        String type = requestDto.type();

        AreaServiceInterface areaService = areaServiceFactory.getService(type);

        Page<GetAreaResponseDto> response = areaService.getArea(requestDto, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto<>(HttpStatus.OK, response));
    }

    @GetMapping("/sync")
    public String syncData() {
        elasticSearchService.syncDataToElasticsearch();
        return "Data synced successfully!";
    }
}
