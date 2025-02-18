package com.ana29.deliverymanagement.area.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import com.ana29.deliverymanagement.area.entity.Area;
import com.ana29.deliverymanagement.area.entity.AreaDocument;
import com.ana29.deliverymanagement.area.repository.AreaRepository;
//import com.ana29.deliverymanagement.area.repository.AreaSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.FilterAggregate;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MultiMatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.indices.AnalyzeRequest;
import org.opensearch.client.opensearch.indices.AnalyzeResponse;
import org.opensearch.client.opensearch.indices.analyze.AnalyzeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AreaService {
    private static final int BATCH_SIZE = 30_000;
    private static final String INDEX_NAME = "area_index";
    private static final String NORI_ANALYZER_NAME = "nori_analyzer";

    private final AreaRepository areaRepository;
    private final OpenSearchClient openSearchClient;
//    private final AreaSearchRepository areaSearchRepository;

    //OpenSearch를 활용한 search 쿼리 생성
    public Map<String, Object> searchArea(String search, int page, int size) throws IOException {
        AnalyzeRequest analyzeRequest = AnalyzeRequest.of(a -> a
                .index(INDEX_NAME)
                .text(search)
                .analyzer(NORI_ANALYZER_NAME)
        );

        //사용자에게 전달 받은 문자열을 Nori 단위로 가져오기 [부산,광역,시]
        AnalyzeResponse noriResponse = openSearchClient.indices().analyze(analyzeRequest);

        List<String> noriTokens = noriResponse.tokens().stream()
                .map(AnalyzeToken::token)
                .toList();

        List<Query> queries = new ArrayList<>();

        for (String token : noriTokens) {
            // 숫자로만 이루어진 토큰이라면 숫자 검색 쿼리 생성
            if (token.chars().allMatch(Character::isDigit)) {
                BoolQuery numberQuery = generateNumberQuery(token);
                queries.add(Query.of(q -> q.bool(b -> b.should(numberQuery.should()))));
                continue;
            }

//            // 숫자 토큰 처리
//            if (token.chars().allMatch(Character::isDigit)) {
//                BoolQuery numberQuery = generateNumberQuery(token);
//                queries.add(Query.of(q -> q.bool(b -> b.should(numberQuery.should()))));
//                continue;
//            }

            // Nori Aggregated Search 실행
            List<Query> aggregatedQueries = performNoriAggregatedSearch(token);
            if (!aggregatedQueries.isEmpty()) {
                BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
                aggregatedQueries.forEach(boolQueryBuilder::should);
                queries.add(Query.of(q -> q.bool(boolQueryBuilder.build())));
            }

//            List<String> fields = getTokenFieldByAggregation(token);

//            if (endsWithSuffixes(token)) { // ex) 서면, 장전동, 부산광역시
//                if (fields.isEmpty()) { // 토큰이 없다면 -> 장전동
//                    List<String> jusoTokens = analyzeText(token, "juso_analyzer"); // 분석하여 -> 장전 / 동
//                    for (String jusoToken : jusoTokens) {
//                        List<Query> searchQueries = search(jusoToken, getTokenFieldByAggregation(jusoToken));
//
//                        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
//                        for (Query query : searchQueries) {
//                            boolQueryBuilder.should(query);
//                        }
//                        queries.add(Query.of(q -> q.bool(boolQueryBuilder.build())));
//                    }
//                } else { // 토큰이 있다면 -> 서면, 부산광역시
//                    List<Query> searchQueries = search(token, fields);
//
//                    BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
//                    for (Query query : searchQueries) {
//                        boolQueryBuilder.should(query);
//                    }
//                    queries.add(Query.of(q -> q.bool(boolQueryBuilder.build())));
//                }
//            } else { // ex) 장전, 현대아파트
//                if (fields.isEmpty()) { // 토큰이 없다면 -> 현대아파트 -> 건물명 검색
//                    MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(m -> m
//                            .query(token)
//                            .fields(Arrays.asList("cityCountyBuildingName.nori", "cityCountyBuildingName.ngram", "cityCountyBuildingName.edge_ngram"))
//                    );
//                    queries.add(Query.of(q -> q.multiMatch(multiMatchQuery)));
//                } else { // 토큰이 있다면 -> 장전
//                    List<Query> searchQueries = search(token, fields);
//
//                    BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
//                    for (Query query : searchQueries) {
//                        boolQueryBuilder.should(query);
//                    }
//                    queries.add(Query.of(q -> q.bool(boolQueryBuilder.build())));
//                }
//            }
        }

        // 검색 요청 생성
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .from(page)
                .size(size)
                .query(q -> q.bool(b -> b.must(queries)))
        );

        // 최종 검색 실행
        SearchResponse<AreaDocument> analyzeResponse2 = openSearchClient.search(searchRequest, AreaDocument.class);

        // 검색 결과 목록 가져오기
        List<Map<String, Object>> documents = analyzeResponse2.hits().hits().stream()
                .map(AreaService::convertAreaDocumentToMap)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();

        // 결과를 Map에 저장
        response.put("totalHits", analyzeResponse2.hits().total().value()); // 전체 검색 개수
        response.put("documents", documents); // 변환된 문서 리스트

        return response;
    }


    public List<Query> search(String token, List<String> fields) throws IOException {
        // 주어진 필드에서 주어진 토큰을 검색하는 쿼리 생성
        List<Query> searchQueries = fields.stream()
                .map(field -> createMatchQuery(field, token))
                .collect(Collectors.toList());

        return searchQueries;
    }

    private Query createMatchQuery(String field, String token) {
        return Query.of(q -> q
                .match(m -> m
                        .field(field)
                        .query(FieldValue.of(token))
                )
        );
    }

    public List<String> analyzeText(String text, String analyzerName) throws IOException {
        AnalyzeRequest analyzeRequest = AnalyzeRequest.of(a -> a
                .text(text)
                .analyzer(analyzerName)  // 분석기 이름
        );

        AnalyzeResponse analyzeResponse = openSearchClient.indices().analyze(analyzeRequest);

        // 분석된 토큰을 반환
        return analyzeResponse.tokens().stream()
                .map(AnalyzeToken::token)
                .collect(Collectors.toList());
    }

    private static Map<String, Object> convertAreaDocumentToMap(Hit<AreaDocument> hit) {
        Map<String, Object> docMap = new HashMap<>();
        AreaDocument document = hit.source();

        if (document != null) {
            docMap.put("roadAddress", document.getRoadAddress());
            docMap.put("jibunAddress", document.getJibunAddress());
            // 필요하면 다른 필드도 추가가능
        }

        return docMap;
    }

    public List<String> getTokenFieldByAggregation(String token) throws IOException {
        // 필드를 찾기 위한 필드 목록 정의
        List<String> fields = List.of(
                "sidoName.nori",
                "cityCountyName.nori",
                "roadName.nori",
                "legalTownName.nori",
                "cityCountyBuildingName.nori"
        );

        // 각 필드에서 해당 토큰이 존재하는지 확인하여 필드를 반환
        List<String> matchingFields = new ArrayList<>();

        for (String field : fields) {
            if (isTokenInField(field, token)) {  // 토큰이 해당 필드에 있는지 검사
                matchingFields.add(field);
            }
        }

        return matchingFields;
    }

    private boolean isTokenInField(String field, String token) throws IOException {
        // OpenSearch에서 해당 필드에 대해 토큰이 존재하는지 확인하는 쿼리 실행
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(Query.of(q -> q
                        .term(t -> t
                                .field(field)
                                .value(v -> v.stringValue(token))
                        )
                ))
        );

        // 쿼리 실행
        SearchResponse<Object> response = openSearchClient.search(searchRequest, Object.class);

        // 검색된 결과가 있는지 확인
        long totalHits = response.hits().total().value();
        return totalHits > 0;  // 필드에서 토큰이 발견되면 true, 아니면 false
    }

    private String generateFullAddressTown(Area area) {
        StringBuilder address = new StringBuilder(area.getCity());

        // district가 존재하면 추가
        if (area.getDistrict() != null && !area.getDistrict().isEmpty()) {
            address.append(" ").append(area.getDistrict());
        }

        // town이 존재하면 추가
        if (area.getTown() != null && !area.getTown().isEmpty()) {
            address.append(" ").append(area.getTown());
        }

        // village가 존재하면 추가
        if (area.getVillage() != null && !area.getVillage().isEmpty()) {
            address.append(" ").append(area.getVillage());
        }

        // 지번 본번 존재하면 추가
        if (area.getTownMainNo() != null && !area.getTownMainNo().isEmpty()) {
            address.append(" ").append(area.getTownMainNo());
        }

        // 지번 부번이 0이 아닐 경우에만 -subNo 추가
        if (area.getTownSubNo() != null && !area.getTownSubNo().equals("0")) {
            address.append("-").append(area.getTownSubNo());
        }

        // 건물명이 존재하면 추가
        if (area.getBuildingName() != null && !area.getBuildingName().isEmpty()) {
            address.append(" (").append(area.getBuildingName()).append(")");
        }

        return address.toString();
    }

    private String generateFullAddressRoadName(Area area) {
        StringBuilder address = new StringBuilder(area.getCity());

        // district가 존재하면 추가
        if (area.getDistrict() != null && !area.getDistrict().isEmpty()) {
            address.append(" ").append(area.getDistrict());
        }

        // town이 존재하면 추가
        if (area.getTown() != null && !area.getTown().isEmpty()) {
            address.append(" ").append(area.getTown());
        }

        // road가 존재하면 추가
        if (area.getRoad() != null && !area.getRoad().isEmpty()) {
            address.append(" ").append(area.getRoad());
        }

        // 도로명 본번이 존재하면 추가
        if (area.getRoadMainNo() != null && !area.getRoadMainNo().isEmpty()) {
            address.append(" ").append(area.getRoadMainNo());
        }

        // 도로명 부번이 0이 아닐 경우에만 -subNo 추가
        if (area.getRoadSubNo() != null && !area.getRoadSubNo().equals("0")) {
            address.append("-").append(area.getRoadSubNo());
        }

        // 건물명이 존재하면 추가
        if (area.getBuildingName() != null && !area.getBuildingName().isEmpty()) {
            address.append(" (").append(area.getBuildingName()).append(")");
        }

        return address.toString();
    }

    public BoolQuery generateNumberQuery(String token) {
        return BoolQuery.of(b -> b
                .should(s -> s.match(m -> m
                        .field("mainBuildingNumber")
                        .query(FieldValue.of(token))))
                .should(s -> s.match(m -> m
                        .field("subBuildingNumber")
                        .query(FieldValue.of(token))))
                .should(s -> s.match(m -> m
                        .field("mainParcelNumber")
                        .query(FieldValue.of(token))))
                .should(s -> s.match(m -> m
                        .field("subParcelNumber")
                        .query(FieldValue.of(token))))
        );
    }

    public boolean endsWithSuffixes(String token) {
        // 접미사 리스트
        List<String> suffixes = Arrays.asList("시", "군", "구", "동", "면", "리", "길", "로", "대로", "가"); // 필요한 접미사들 추가

        for (String suffix : suffixes) {
            if (token.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    public List<Query> performNoriAggregatedSearch(String token) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .size(0)
                .aggregations("sidoName_hits", a -> a
                        .filter(f -> f.match(m -> m
                                .field("sidoName.nori")
                                .query(FieldValue.of(token))))
                )
                .aggregations("cityCountyName_hits", a -> a
                        .filter(f -> f.match(m -> m
                                .field("cityCountyName.nori")
                                .query(FieldValue.of(token))))
                )
                .aggregations("roadName_hits", a -> a
                        .filter(f -> f.match(m -> m
                                .field("roadName.nori")
                                .query(FieldValue.of(token))))
                )
                .aggregations("legalTownName_hits", a -> a
                        .filter(f -> f.match(m -> m
                                .field("legalTownName.nori")
                                .query(FieldValue.of(token))))
                )
                .aggregations("cityCountyBuildingName_hits", a -> a
                        .filter(f -> f.match(m -> m
                                .field("cityCountyBuildingName.nori")
                                .query(FieldValue.of(token))))
                )
        );

        SearchResponse<Void> response = openSearchClient.search(searchRequest, Void.class);

        // Aggregation 결과에서 doc_count가 0이 아닌 필드 찾기
        List<String> nonZeroDocCountFields = findNonZeroDocCountFields(response);

        List<Query> queries = new ArrayList<>();
        for (String fieldName : nonZeroDocCountFields) {
            switch (fieldName) {
                case "sidoName_hits":
                    queries.add(Query.of(q -> q.term(t -> t.field("sidoName.nori").value(FieldValue.of(token)))));
                    break;
                case "cityCountyName_hits":
                    queries.add(Query.of(q -> q.term(t -> t.field("cityCountyName.nori").value(FieldValue.of(token)))));
                    break;
                case "roadName_hits":
                    queries.add(Query.of(q -> q.term(t -> t.field("roadName.nori").value(FieldValue.of(token)))));
                    break;
                case "legalTownName_hits":
                    queries.add(Query.of(q -> q.term(t -> t.field("legalTownName.nori").value(FieldValue.of(token)))));
                    break;
                case "cityCountyBuildingName_hits":
                    queries.add(Query.of(q -> q.multiMatch(m -> m
                            .query(token)
                            .fields(List.of(
                                    "cityCountyBuildingName.ngram",
                                    "cityCountyBuildingName.edge_ngram",
                                    "cityCountyBuildingName.nori"
                            ))
                    )));
                    break;
            }
        }
        return queries;
    }

    public static List<String> findNonZeroDocCountFields(SearchResponse<Void> response) {
        Map<String, Aggregate> aggregations = response.aggregations();

        return aggregations.entrySet().stream()
                .filter(entry -> entry.getValue()._kind() == Aggregate.Kind.Filter) // FilterAggregate만 필터링
                .map(entry -> new FieldCount(entry.getKey(), ((FilterAggregate) entry.getValue()._get()).docCount()))
                .filter(fieldCount -> fieldCount.docCount > 0) // doc_count가 0보다 큰 것만 필터링
                .map(fieldCount -> fieldCount.field) // 필드 이름 추출
                .collect(Collectors.toList());
    }

    private static class FieldCount {
        String field;
        long docCount;

        FieldCount(String field, long docCount) {
            this.field = field;
            this.docCount = docCount;
        }
    }

    // 데이터 밀어 넣기
    // 한 번에 처리할 데이터 크기 30,000건
    public void syncDataToElasticsearch() {
//        int page = 0;
//        Page<Area> areas;
//
//        do {
//            areas = areaRepository.findAll(PageRequest.of(page, BATCH_SIZE));
//
//            List<AreaDocument> documents = areas.getContent().stream()
//                    .map(area -> AreaDocument.builder()
////                            .id(area.getId().toString())
//                            .roadAddress(generateFullAddressRoadName(area))
//                            .jibunAddress(generateFullAddressTown(area))
//                            .sidoName(area.getCity())
//                            .cityCountyName(area.getDistrict())
//                            .legalTownName(area.getTown())
//                            .mainParcelNumber(area.getTownMainNo())
//                            .subParcelNumber(area.getTownSubNo())
//                            .roadName(area.getRoad())
//                            .mainBuildingNumber(area.getRoadMainNo())
//                            .subBuildingNumber(area.getRoadSubNo())
//                            .cityCountyBuildingName(area.getBuildingName())
//                            .build())
//                    .collect(Collectors.toList());
//
//            areaSearchRepository.saveAll(documents);
//            page++;
//
//            System.out.println("Processed Page: " + page);
//
//        } while (areas.hasNext()); // 다음 페이지가 있을 때까지 반복
    }
}