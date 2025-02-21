package com.ana29.deliverymanagement.review.dto;

import java.time.LocalDate;

public record ReviewSearchCondition(String keyword,
                                    LocalDate startDate,
                                    LocalDate endDate,
                                    Boolean isAsc) {
    public ReviewSearchCondition {
        isAsc = isAsc != null ? isAsc : false;
    }
}
