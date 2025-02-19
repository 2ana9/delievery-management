package com.ana29.deliverymanagement.area.controller;

import com.ana29.deliverymanagement.area.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/area")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchAreas(@RequestParam("search") String search,
                                                           @RequestParam("page") int page,
                                                            @RequestParam("size") int size) throws IOException {
        Map<String, Object> result = areaService.searchArea(search, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sync")
    public String syncData() {
        areaService.syncDataToElasticsearch();
        return "Data synced successfully!";
    }
}
