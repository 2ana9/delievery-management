package com.ana29.deliverymanagement.restaurant.controller;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.restaurant.service.MenuService;
import com.ana29.deliverymanagement.restaurant.dto.MenuDto;
import com.ana29.deliverymanagement.restaurant.dto.MenuRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.MenuUpdateRequestDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;


    @PostMapping("/add")
    public ResponseEntity<ResponseDto<MenuDto>> createMenu(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid MenuRequestDto menuRequestDto) {
        MenuDto createdMenu = menuService.createMenu(userDetails.getId(), menuRequestDto);
        return ResponseEntity.ok(ResponseDto.success(createdMenu));
    }
//    {
//        "name": "맛있는 메뉴",
//            "price": 15000,
//            "description": "매콤달콤한 맛이 일품인 메뉴",
//            "restaurantId": "550e8400-e29b-41d4-a716-446655440000"
//    }

//     메뉴 단건 조회

    @GetMapping("/{menuId}")
    public ResponseEntity<ResponseDto<MenuDto>> getMenu(
            @PathVariable UUID menuId) {
        MenuDto menu = menuService.getMenu(menuId);
        return ResponseEntity.ok(ResponseDto.success(menu));
    }


    @PatchMapping("/{menuId}")
    public ResponseEntity<ResponseDto<MenuDto>> updateMenu(
            @PathVariable UUID menuId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid MenuUpdateRequestDto requestDto) {
        MenuDto updatedMenu = menuService.updateMenu(menuId, userDetails.getId(), requestDto);
        return ResponseEntity.ok(ResponseDto.success(updatedMenu));
    }
//    {
//        "name": "새로운 메뉴 이름",
//            "price": 15000,
//            "description": "맛있는 메뉴 설명 예시"
//    }


    @DeleteMapping("/{menuId}")
    public ResponseEntity<ResponseDto<MenuDto>> deleteMenu(
            @PathVariable UUID menuId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        MenuDto deletedMenu = menuService.deleteMenu(menuId, userDetails.getId());
        return ResponseEntity.ok(ResponseDto.success(deletedMenu));
    }

    //     메뉴 전체 조회
    @GetMapping
    public ResponseEntity<ResponseDto<List<MenuDto>>> getAllMenus(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "true") boolean isAsc) {

        List<MenuDto> menus = menuService.getAllMenus(userDetails.getId(), page, size, sortBy, isAsc);
        return ResponseEntity.ok(ResponseDto.success(menus));
    }
}
