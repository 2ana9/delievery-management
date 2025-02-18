package com.ana29.deliverymanagement.restaurant.controller;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderHistoryResponseDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantResponseDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.restaurant.service.RestaurantService;
import com.ana29.deliverymanagement.user.constant.user.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

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
    @PutMapping("{id}")
    public RestaurantResponseDto updateRestaurant(@PathVariable UUID id, @RequestBody RestaurantRequestDto restaurantRequestDto
            , @AuthenticationPrincipal UserDetailsImpl userDetails)throws AccessDeniedException{
        //수정은 관리자도 가능하고 가게사장도 가능하게 구현
        UserRoleEnum userRole = userDetails.getUser().getRole();
        if (userRole == UserRoleEnum.CUSTOMER) {
            throw new AccessDeniedException("관리자 접근이 필요합니다.");
        }
        return restaurantService.updateRestaurant(id, restaurantRequestDto);
    };

    //가게 조회 메소드 (전체)
    @GetMapping
    public ResponseEntity<ResponseDto<Page<RestaurantResponseDto>>> getAllRestaurant(Pageable pageable){

        Page<RestaurantResponseDto> response =
                restaurantService.getAllRestaurant(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto<>(HttpStatus.OK, response));
    }

    //가게 삭제메소드(관리자,가게사장)

    //사용자의 권한확인 메소드
    public void checkUserAccess(UserDetailsImpl userDetails) throws AccessDeniedException {
        UserRoleEnum userRole = userDetails.getUser().getRole();
        if (userRole != UserRoleEnum.MASTER) {
            throw new AccessDeniedException("관리자 접근이 필요합니다.");
        }
    };

}
