package com.ana29.deliverymanagement.area.repository;

import com.ana29.deliverymanagement.area.entity.AreaDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

//public interface AreaSearchRepository extends ElasticsearchRepository<AreaDocument, String> {
//    // 커스텀 쿼리 메서드 (복합적인 검색을 위해)
//    @Query("{\"bool\": {\"should\": ["
//            + "{\"match\": {\"fullAddressTown\": \"?0\"}}, "
//            + "{\"match\": {\"fullAddressRoadName\": \"?0\"}} ]}}")
//    Page<AreaDocument> findByFullAddressContaining(String search, Pageable pageable);
//}
