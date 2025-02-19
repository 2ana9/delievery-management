package com.ana29.deliverymanagement.area.repository;

import com.ana29.deliverymanagement.area.entity.AreaDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AreaSearchRepository extends ElasticsearchRepository<AreaDocument, String> {

}