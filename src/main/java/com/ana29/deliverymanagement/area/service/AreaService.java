package com.ana29.deliverymanagement.area.service;

import com.ana29.deliverymanagement.area.entity.Area;
import com.ana29.deliverymanagement.area.entity.AreaDocument;
import com.ana29.deliverymanagement.area.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AreaService {
    private static final int BATCH_SIZE = 10_000;

    private final AreaRepository areaRepository;
//    private final AreaSearchRepository areaSearchRepository;

    public Map<String, Object> searchArea(String search, int page, int size) {
        // Pageable 생성
        Pageable pageable = PageRequest.of(page, size);

        // 검색 결과 페이징 처리
//        Page<AreaDocument> resultPage = areaSearchRepository.findByFullAddressContaining(search, pageable);

        // 임시 데이터를 생성
        List<AreaDocument> areaDocuments = new ArrayList<>();
        areaDocuments.add(new AreaDocument("1", "울산광역시 남구 무거동 316-2", "울산광역시 남구 울산고속도로 15-0"));
        areaDocuments.add(new AreaDocument("2", "서울특별시 종로구 청운동 50-6", "서울특별시 종로구 자하문로 115-14"));
        areaDocuments.add(new AreaDocument("3", "부산광역시 해운대구 우동 150-5", "부산광역시 해운대구 해운대해변로 5"));

        Page<AreaDocument> resultPage = new PageImpl<>(areaDocuments, pageable, areaDocuments.size());

        // 응답에 필요한 데이터 준비
        Map<String, Object> response = new HashMap<>();
        response.put("totalElements", resultPage.getTotalElements());
        response.put("totalPages", resultPage.getTotalPages());
        response.put("currentPage", resultPage.getNumber());
        response.put("pageSize", resultPage.getSize());
        response.put("content", resultPage.getContent());

        return response;
    }

    // 한 번에 처리할 데이터 크기 10,000건
    public void syncDataToElasticsearch() {
        int page = 0;
        Page<Area> areas;

        do {
            areas = areaRepository.findAll(PageRequest.of(page, BATCH_SIZE));

            List<AreaDocument> documents = areas.getContent().stream()
                    .map(area -> AreaDocument.builder()
                            .id(area.getId().toString())
                            .fullAddressTown(generateFullAddressTown(area))
                            .fullAddressRoadName(generateFullAddressRoadName(area))
                            .build())
                    .collect(Collectors.toList());

//            areaSearchRepository.saveAll(documents); // Elasticsearch에 저장
            page++;

            System.out.println("Processed Page: " + page);

        } while (areas.hasNext()); // 다음 페이지가 있을 때까지 반복
    }

    private String generateFullAddressTown(Area area) {
        return String.format("%s %s %s %s-%s",
                area.getCity(), area.getDistrict(), area.getTown(),
                area.getLot_main_no(), area.getLot_sub_no());
    }

    private String generateFullAddressRoadName(Area area) {
        return String.format("%s %s %s %s-%s",
                area.getCity(), area.getDistrict(), area.getRoad_name(),
                area.getBuilding_main_no(), area.getBuilding_sub_no());
    }
}