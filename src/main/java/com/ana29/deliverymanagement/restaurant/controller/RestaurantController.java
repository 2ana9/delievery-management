package com.ana29.deliverymanagement.restaurant.controller;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.restaurant.dto.RestaurantRequestDto;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.restaurant.service.RestaurantService;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        UserRoleEnum userRole = userDetails.getRole();
        if (userRole != UserRoleEnum.MASTER && userRole != UserRoleEnum.MANAGER) {
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
        String userId = userDetails.getId();
        return restaurantService.updateRestaurant(id, requestDto, userId);
    };

    //가게 삭제메소드(관리자,가게사장)
    @DeleteMapping("/{id}")
    public ResponseDto<Restaurant>
    deleteRestaurant(@PathVariable UUID id,
                     @AuthenticationPrincipal UserDetailsImpl userDetails)
            throws AccessDeniedException{
        checkUserAccess(userDetails);
        String userId = userDetails.getId();
        return restaurantService.deleteRestaurant(id,userId);
    }

    //search
    @GetMapping("/search")
    public ResponseDto<List<Object>> searchRestaurants( //검색기능에 평점도 함께 조회
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String legalCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort){

        Sort sortValue = checkSortValue(sort); //정렬기준 체크

        if(size != 10 && size != 30 && size != 50){//10건 30건 50건 외에는 10건 고정
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size, sortValue);

        return restaurantService.searchRestaurants(name,categoryId,legalCode,pageable);
    }

    //생성일순,수정일순 정렬체크
    private Sort checkSortValue(String sort) {
        String[] sortParams = sort.split(",");
        String sortType = sortParams[0];//정렬기준
        String sortDirection = sortParams[1];//asc,desc

        if (sortDirection.equalsIgnoreCase("desc")) {
            return Sort.by(Sort.Order.desc(sortType));
        } else {
            return Sort.by(Sort.Order.asc(sortType));
        }
    }


    //사용자의 권한확인 메소드
    public void checkUserAccess(UserDetailsImpl userDetails) throws AccessDeniedException {
        UserRoleEnum userRole = userDetails.getRole();
        if (userRole != UserRoleEnum.MASTER && userRole != UserRoleEnum.OWNER) {
            throw new AccessDeniedException("관리자 접근이 필요합니다.");
        }
    };

}
