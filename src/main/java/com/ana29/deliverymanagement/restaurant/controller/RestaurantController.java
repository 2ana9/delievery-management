package com.ana29.deliverymanagement.restaurant.controller;

import com.ana29.deliverymanagement.security.constant.user.UserRoleEnum;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantResponseDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public RestaurantResponseDto createRestaurant(@RequestBody RestaurantRequestDto restaurantRequestDto,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        checkUserAccess(userDetails);
        return restaurantService.createRestaurant(restaurantRequestDto);
    };

    //사용자의 권한확인 메소드
    public void checkUserAccess(UserDetailsImpl userDetails) throws AccessDeniedException {
        UserRoleEnum userRole = userDetails.getUser().getRole();
        if (userRole != UserRoleEnum.ADMIN) {
            throw new AccessDeniedException("관리자 접근이 필요합니다.");
        }
    };

}
