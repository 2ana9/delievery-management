package com.ana29.deliverymanagement.area.repository;

import com.ana29.deliverymanagement.area.dto.GetAreaRequestDto;
import com.ana29.deliverymanagement.area.dto.GetAreaResponseDto;
import com.ana29.deliverymanagement.area.entity.QArea;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.ana29.deliverymanagement.area.entity.QArea.area;

@RequiredArgsConstructor
public class AreaRepositoryCustomImpl implements AreaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<GetAreaResponseDto> findAreas(GetAreaRequestDto condition, Pageable pageable) {
        QArea area = QArea.area;

        List<GetAreaResponseDto> content = queryFactory
                .select(Projections.constructor(GetAreaResponseDto.class,
                        Expressions.stringTemplate("CONCAT_WS(' ', {0}, {1}, {2}, {3}, {4}, {5}, {6})",
                                area.city, area.district, area.town, area.village,
                                area.townMainNo, area.townSubNo, area.buildingName),
                        Expressions.stringTemplate("CONCAT_WS(' ', {0}, {1}, {2}, {3}, {4}, {5})",
                                area.city, area.district, area.road, area.roadMainNo, area.roadSubNo, area.buildingName)
                ))
                .from(area)
                .where(keywordContains(condition.keyword()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long fetchedCount = queryFactory
                .select(area.count())
                .from(area)
                .fetchOne();

        long total = fetchedCount != null ? fetchedCount : 0;

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        return area.roadAddress.contains(keyword)
                .or(area.jibunAddress.contains(keyword));
    }
}
