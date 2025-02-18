package com.ana29.deliverymanagement.area.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true) //_class 필드 무시
public class AreaDocument {
    @Id
    private String id;

//    @Field(type = FieldType.Text, analyzer = "whitespace_analyzer", searchAnalyzer = "whitespace_analyzer")
//    private String fullAddressTown;     // 서울특별시 종로구 청운동 50-6
//    @Field(type = FieldType.Text, analyzer = "whitespace_analyzer", searchAnalyzer = "whitespace_analyzer")
//    private String fullAddressRoadName; // 서울특별시 종로구 자하문로 115-14

    @Field(type = FieldType.Text, analyzer = "whitespace_analyzer", searchAnalyzer = "whitespace_analyzer")
    private String roadAddress; // 지번 주소 (예: 서울특별시 종로구 청운동 50-6)

    @Field(type = FieldType.Text, analyzer = "whitespace_analyzer", searchAnalyzer = "whitespace_analyzer")
    private String jibunAddress; // 도로명 주소 (예: 서울특별시 종로구 자하문로 115-14)

    @Field(type = FieldType.Text, analyzer = "nori_analyzer", searchAnalyzer = "nori_analyzer")
    private String sidoName; // 시도명 (예: 서울특별시)

    @Field(type = FieldType.Text, analyzer = "nori_analyzer", searchAnalyzer = "nori_analyzer")
    private String cityCountyName; // 구/군 명 (예: 종로구)

    @Field(type = FieldType.Text, analyzer = "nori_analyzer", searchAnalyzer = "nori_analyzer")
    private String legalTownName; // 법정동 (예: 청운동)

    @Field(type = FieldType.Text)
    private String mainParcelNumber; //지번 본번 예) (50)
    @Field(type = FieldType.Text)
    private String subParcelNumber; //지번 부번 예) (6)

    @Field(type = FieldType.Text, analyzer = "nori_analyzer", searchAnalyzer = "nori_analyzer")
    private String roadName; // 도로명 (예: 자하문로)

    @Field(type = FieldType.Text)
    private String mainBuildingNumber; //도로명 본번 예) (115)
    @Field(type = FieldType.Text)
    private String subBuildingNumber; //도로명 부번 예) (14)

    @Field(type = FieldType.Text, analyzer = "edge_ngram_analyzer", searchAnalyzer = "edge_ngram_analyzer")
    private String cityCountyBuildingName; // 건물명 (예: 카이저빌)
}
