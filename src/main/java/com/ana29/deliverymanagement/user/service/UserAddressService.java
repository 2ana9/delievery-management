package com.ana29.deliverymanagement.user.service;

import com.ana29.deliverymanagement.order.dto.OrderHistoryResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderSearchCondition;
import com.ana29.deliverymanagement.order.exception.OrderForbiddenException;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.user.dto.*;
import com.ana29.deliverymanagement.user.entity.User;
import com.ana29.deliverymanagement.user.entity.UserAddress;
import com.ana29.deliverymanagement.user.exception.DuplicateAddressException;
import com.ana29.deliverymanagement.user.repository.UserAddressRepository;
import com.ana29.deliverymanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;

    public CreateUserAddressResponseDto createUserAddress(CreateUserAddressRequestDto requestDto, UserDetailsImpl userDetails) {
        // 로그인한 유저 정보 가져오기
        User user = userDetails.getUser();

        // 새 주소에서 공백 제거
        String normalizedAddress = removeWhitespace(requestDto.address());

        // 해당 유저의 기존 배송지 목록 조회
        List<UserAddress> userAddressList = userAddressRepository.findByUser(user);

        // 기존 배송지와 공백 제거 후 비교하여 중복 체크
        boolean isDuplicate = userAddressList.stream()
                .map(existingAddress -> removeWhitespace(existingAddress.getAddress()))
                .anyMatch(existing -> existing.equals(normalizedAddress));

        if (isDuplicate) {
            throw new DuplicateAddressException("이미 등록된 배송지입니다.");
        }

        // 중복이 없으면 새로운 배송지 저장
        UserAddress userAddress = userAddressRepository.save(UserAddress.builder()
                .user(user)
                .address(requestDto.address()) // 원본 주소 저장
                .build());

        return new CreateUserAddressResponseDto(userAddress.getId(), userAddress.getAddress());
    }

    // 문자열 공백 제거 메서드
    private String removeWhitespace(String input) {
        return input.replaceAll("\\s+", "");
    }

    @Transactional(readOnly = true)
    public Page<GetUserAddressesResponseDto> getUserAddresses(GetUserAddressesRequestDto condition, Pageable pageable, UserDetailsImpl userDetails) {
        return userAddressRepository.findUserAddresses(userDetails, condition, pageable);
    }
}
