package com.ana29.deliverymanagement.user.repository;

import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.user.dto.GetUserAddressesRequestDto;
import com.ana29.deliverymanagement.user.dto.GetUserAddressesResponseDto;
import com.ana29.deliverymanagement.user.entity.QUserAddress;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class UserAddressRepositoryCustomImpl implements UserAddressRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<GetUserAddressesResponseDto> findUserAddresses(UserDetailsImpl userDetails, GetUserAddressesRequestDto condition, Pageable pageable) {
        // 로그인한 유저 정보 가져오기
        String userId = userDetails.getUsername();

        QUserAddress userAddress = QUserAddress.userAddress;

        List<GetUserAddressesResponseDto> content = queryFactory
                .select(Projections.constructor(GetUserAddressesResponseDto.class,
                        userAddress.id,
                        userAddress.address,
                        userAddress.detail,
                        userAddress.defaultAddress))
                .from(userAddress)
                .where(
                        userAddress.isDeleted.isFalse(),
                        userAddress.user.Id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long fetchedCount = queryFactory
                .select(userAddress.count())
                .from(userAddress)
                .where(
                        userAddress.isDeleted.isFalse(),
                        userAddress.user.Id.eq(userId)
                )
                .fetchOne();

        long total = fetchedCount != null ? fetchedCount : 0;

        return new PageImpl<>(content, pageable, total);
    }
}
