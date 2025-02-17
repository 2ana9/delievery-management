package com.ana29.deliverymanagement.restaurant.controller;

import com.ana29.deliverymanagement.security.constant.user.UserRoleEnum;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantResponseDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    //가게 추가 메소드(관리자)
    @PostMapping
    public RestaurantResponseDto createRestaurant(@RequestBody RestaurantRequestDto restaurantRequestDto,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        checkUserAccess(userDetails);
        return restaurantService.createRestaurant(restaurantRequestDto);
    };

    //가게 수정 메소드(관리자,가게사장)
//    @PutMapping()
//    public RestaurantResponseDto updateRestaurant(@RequestBody RestaurantRequestDto restaurantRequestDto
//            ,@AuthenticationPrincipal UserDetailsImpl userDetails)throws AccessDeniedException{
//      //수정은 관리자도 가능하고 가게사장도 가능하게 구현
//        UserRoleEnum userRole = userDetails.getUser().getRole();
//        if (userRole != UserRoleEnum.ADMIN && userRole != UserRoleEnum.) {
//            throw new AccessDeniedException("관리자 접근이 필요합니다.");
//        }
//    };

    //가게 조회 메소드 (전체)

    //가게 삭제메소드(관리자,가게사장)

    //사용자의 권한확인 메소드
    public void checkUserAccess(UserDetailsImpl userDetails) throws AccessDeniedException {
        UserRoleEnum userRole = userDetails.getUser().getRole();
        if (userRole != UserRoleEnum.ADMIN) {
            throw new AccessDeniedException("관리자 접근이 필요합니다.");
        }
    };

}
