package com.ana29.deliverymanagement.menu.controller;

import com.ana29.deliverymanagement.menu.dto.MenuDto;
import com.ana29.deliverymanagement.menu.dto.MenuRequestDto;
import com.ana29.deliverymanagement.menu.dto.MenuUpdateRequestDto;
import com.ana29.deliverymanagement.menu.service.MenuService;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/add")
    public ResponseEntity<MenuDto> createMenu(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid MenuRequestDto menuRequestDto) {
        MenuDto createdMenu = menuService.createMenu(userDetails.getId(), menuRequestDto);
        return ResponseEntity.ok(createdMenu);
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuDto> getMenu(
            @PathVariable UUID menuId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        MenuDto menu = menuService.getMenu(menuId, userDetails.getId());
        return ResponseEntity.ok(menu);
    }

    @PatchMapping("/{menuId}")
    public ResponseEntity<MenuDto> updateMenu(
            @PathVariable UUID menuId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid MenuUpdateRequestDto requestDto) {
        MenuDto updatedMenu = menuService.updateMenu(menuId, userDetails.getId(), requestDto);
        return ResponseEntity.ok(updatedMenu);
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<MenuDto> deleteMenu(
            @PathVariable UUID menuId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        MenuDto deletedMenu = menuService.deleteMenu(menuId, userDetails.getId());
        return ResponseEntity.ok(deletedMenu);
    }

    @GetMapping
    public ResponseEntity<Page<MenuDto>> getAllMenus(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "true") boolean isAsc) {

        Page<MenuDto> menus = menuService.getAllMenus(userDetails.getId(), page, size, sortBy, isAsc);
        return ResponseEntity.ok(menus);
    }
}
