package com.ana29.deliverymanagement.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "area_index")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaDocument {
    @Id
    private String id;

    //자동으로 텍스트 분석기를 사용하여 검색어를 분리하고 색인화
    //부분적으로 일치하는 값을 검색할수 있음
    @Field(type = FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer")
    private String fullAddressTown;     // 서울특별시 종로구 청운동 50-6
    @Field(type = FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer")
    private String fullAddressRoadName; // 서울특별시 종로구 자하문로 115-14
}
