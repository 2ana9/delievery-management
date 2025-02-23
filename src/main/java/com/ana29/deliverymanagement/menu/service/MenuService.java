package com.ana29.deliverymanagement.menu.service;

import com.ana29.deliverymanagement.common.util.PagingUtil;
import com.ana29.deliverymanagement.menu.dto.MenuDto;
import com.ana29.deliverymanagement.menu.dto.MenuRequestDto;
import com.ana29.deliverymanagement.menu.dto.MenuUpdateRequestDto;
import com.ana29.deliverymanagement.menu.entity.Menu;
import com.ana29.deliverymanagement.menu.repository.MenuRepository;
import com.ana29.deliverymanagement.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional
    public MenuDto createMenu(String ownerId, MenuRequestDto requestDto) {
        Menu menu = Menu.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .restaurantId(requestDto.getRestaurantId())
                .ownerId(ownerId)
                .isDeleted(false)
                .build();
        Menu saved = menuRepository.save(menu);
        return convertToDto(saved);
    }

    @Transactional(readOnly = true)
    public MenuDto getMenu(UUID menuId, String ownerId) {
        Menu menu = menuRepository.findByIdAndOwnerIdAndIsDeletedFalse(menuId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없거나 접근 권한이 없습니다."));
        return convertToDto(menu);
    }

    @Transactional
    public MenuDto updateMenu(UUID menuId, String ownerId, MenuUpdateRequestDto requestDto) {
        Menu menu = menuRepository.findByIdAndOwnerIdAndIsDeletedFalse(menuId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없거나 접근 권한이 없습니다."));
        menu.setName(requestDto.getName());
        menu.setPrice(requestDto.getPrice());
        menu.setDescription(requestDto.getDescription());
        // 업데이트 시 사용자의 ID를 updatedBy에 설정 (Userservice와 유사한 패턴)
        menu.setUpdatedBy(ownerId);
        Menu updated = menuRepository.save(menu);
        return convertToDto(updated);
    }

    @Transactional
    public MenuDto deleteMenu(UUID menuId, String ownerId) {
        Menu menu = menuRepository.findByIdAndOwnerIdAndIsDeletedFalse(menuId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없거나 접근 권한이 없습니다."));
        // soft delete 처리
        menu.delete(true);
        // 업데이트 시 사용자의 ID를 deletedBy에 기록할 수 있다면 추가
        menu.setDeletedBy(ownerId);
        Menu deleted = menuRepository.save(menu);
        return convertToDto(deleted);
    }

    @Transactional(readOnly = true)
    public Page<MenuDto> getAllMenus(String ownerId, int page, int size, String sortBy, boolean isAsc) {
        Pageable pageable = userInfoPaging(page, size, sortBy, isAsc);
        Page<Menu> menus = menuRepository.findAllByOwnerIdAndIsDeletedFalse(ownerId, pageable);
        return menus.map(this::convertToDto);
    }

    private MenuDto convertToDto(Menu menu) {
        return MenuDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .isDeleted(menu.isDeleted())
                .build();
    }

    private List<User> userInfoPaging(int page, int size, String sortBy, boolean isAsc) {
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable).getContent();
    }
}
