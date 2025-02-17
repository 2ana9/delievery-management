package com.ana29.deliverymanagement.area.controller;

import com.ana29.deliverymanagement.area.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/area")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchAreas(@RequestParam("search") String search,
                                                           @RequestParam("page") int page,
                                                           @RequestParam("size") int size) {
        Map<String, Object> result = areaService.searchArea(search, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sync")
    public String syncData() {
        areaService.syncDataToElasticsearch();
        return "Data synced successfully!";
    }
}
