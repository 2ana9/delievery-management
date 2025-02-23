package com.ana29.deliverymanagement.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDto {
    private long totalElements;       // 전체 데이터 수
    private int totalPages;           // 전체 페이지 수
    private int currentPage;          // 현재 페이지 번호
    private int pageSize;             // 페이지 크기
    private boolean first;            // 첫 페이지 여부
    private boolean last;             // 마지막 페이지 여부
    private int numberOfElements;     // 현재 페이지 데이터 개수
    private boolean empty;            // 페이지가 비어있는지 여부
}
