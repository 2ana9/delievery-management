package com.ana29.deliverymanagement.user.repository;

import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.user.dto.GetUserAddressesRequestDto;
import com.ana29.deliverymanagement.user.dto.GetUserAddressesResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserAddressRepositoryCustom {
    Page<GetUserAddressesResponseDto> findUserAddresses(UserDetailsImpl user,
                                                        GetUserAddressesRequestDto condition,
                                                        Pageable pageable);
}