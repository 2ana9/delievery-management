package com.ana29.deliverymanagement.restaurant.controller;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantResponseDto;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.restaurant.service.RestaurantService;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.user.constant.user.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    //가게 추가 메소드(관리자,매니저)
    @PostMapping
    public ResponseDto<Restaurant> createRestaurant(@RequestBody RestaurantRequestDto requestDto,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        UserRoleEnum userRole = userDetails.getUser().getRole();
        if (userRole != UserRoleEnum.MASTER || userRole != UserRoleEnum.MANAGER) {
            throw new AccessDeniedException("관리자 접근이 필요합니다.");
        }

        return restaurantService.createRestaurant(requestDto);
    };

    //가게 수정 메소드(관리자,가게사장)
    @PutMapping("/{id}")
    public ResponseDto<Restaurant> updateRestaurant(@PathVariable UUID id, @RequestBody RestaurantRequestDto requestDto
            , @AuthenticationPrincipal UserDetailsImpl userDetails)throws AccessDeniedException{
        //수정은 관리자도 가능하고 가게사장도 가능하게 구현

        checkUserAccess(userDetails);
        String userId = userDetails.getUser().getId();
        return restaurantService.updateRestaurant(id, requestDto, userId);
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
    @DeleteMapping("/{id}")
    public ResponseDto<Restaurant>
    deleteRestaurant(@PathVariable UUID id,
                     @AuthenticationPrincipal UserDetailsImpl userDetails)
            throws AccessDeniedException{
        checkUserAccess(userDetails);
        String userId = userDetails.getUser().getId();
        return restaurantService.deleteRestaurant(id,userId);
    }

    //search
    @GetMapping("/search")
    public ResponseDto<List<Restaurant>> searchRestaurants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Long areaId, Pageable pageable){ //페이징기능 단건 10건으로 수정

        return restaurantService.searchRestaurants(name,categoryId,areaId,pageable);
    };


    //사용자의 권한확인 메소드
    public void checkUserAccess(UserDetailsImpl userDetails) throws AccessDeniedException {
        UserRoleEnum userRole = userDetails.getUser().getRole();
        if (userRole != UserRoleEnum.MASTER || userRole != UserRoleEnum.OWNER) {
            throw new AccessDeniedException("관리자 접근이 필요합니다.");
        }
    };

}
