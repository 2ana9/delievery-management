package com.ana29.deliverymanagement.area.controller;

import com.ana29.deliverymanagement.area.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.AnalyzeRequest;
import org.opensearch.client.opensearch.indices.AnalyzeResponse;
import org.opensearch.client.opensearch.indices.analyze.AnalyzeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
