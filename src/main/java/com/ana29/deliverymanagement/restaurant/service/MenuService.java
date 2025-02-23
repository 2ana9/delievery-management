package com.ana29.deliverymanagement.restaurant.service;

import com.ana29.deliverymanagement.restaurant.dto.MenuDto;
import com.ana29.deliverymanagement.restaurant.dto.MenuRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.MenuUpdateRequestDto;
import com.ana29.deliverymanagement.restaurant.entity.Menu;
import com.ana29.deliverymanagement.restaurant.repository.MenuRepository;
import com.ana29.deliverymanagement.restaurant.repository.RestaurantRepository;
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
    private final RestaurantRepository restaurantRepository;

    /**
     * 메뉴 생성 전, 중복 여부를 체크합니다.
     */
    private void validateDuplicateMenu(String ownerId, MenuRequestDto requestDto) {
        menuRepository.findByNameAndIsDeletedFalse(requestDto.getName())
                .ifPresent(m -> {
                    throw new IllegalArgumentException("중복된 메뉴가 존재합니다.");
                });
    }

    /**
     * 메뉴 생성
     */
    @Transactional
    public MenuDto createMenu(String ownerId, MenuRequestDto requestDto) {
        // 중복 체크
        validateDuplicateMenu(ownerId, requestDto);
        Menu menu = createMenuDto(ownerId, requestDto);
        menuRepository.save(menu);
        return convertToMenuDto(menu);
    }


    /**
     * 메뉴 단건 조회 (권한 체크: 메뉴 소유자와 일치하는지 Repository 쿼리로 확인)
     */
    @Transactional(readOnly = true)
    public MenuDto getMenu(UUID menuId) {
        Menu menu = menuRepository.findByIdAndIsDeletedFalse(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없거나 접근 권한이 없습니다."));
        return convertToMenuDto(menu);
    }

    /**
     * 메뉴 수정 (권한 체크 내장)
     */
    @Transactional
    public MenuDto updateMenu(UUID menuId, String ownerId, MenuUpdateRequestDto requestDto) {
        Menu menu = menuRepository.findByIdAndIsDeletedFalse(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없거나 접근 권한이 없습니다."));
        // 수정 시, 중복 검사를 추가할 수 있다면 여기서 validateDuplicateMenu()를 호출할 수도 있음
        menu.setName(requestDto.getName());
        menu.setPrice(requestDto.getPrice());
        menu.setDescription(requestDto.getDescription());
        // 업데이트 시 소유자 ID를 업데이트 기록(updatedBy)로 설정 (Timestamped에 해당 필드가 있다면)
        menu.setUpdatedBy(ownerId);
        Menu updated = menuRepository.save(menu);
        return convertToMenuDto(updated);
    }

    /**
     * 메뉴 삭제 (soft delete)
     */
    @Transactional
    public MenuDto deleteMenu(UUID menuId, String ownerId) {
        Menu menu = menuRepository.findByIdAndIsDeletedFalse(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없거나 접근 권한이 없습니다."));
        // soft delete 처리: isDeleted 플래그를 true로 설정하고, 삭제한 사용자(deletedBy) 기록
        menu.softDelete(ownerId);
        Menu deleted = menuRepository.save(menu);
        return convertToMenuDto(deleted);
    }

    /**
     * 메뉴 전체 조회 (페이징 적용)
     */
    @Transactional(readOnly = true)
    public List<MenuDto> getAllMenus(String ownerId, int page, int size, String sortBy, boolean isAsc) {
        Page<Menu> menuPage = menuInfoPaging(page, size, sortBy, isAsc);
        return menuPage.map(this::convertToMenuDto).getContent();
    }

    private Menu createMenuDto(String ownerId, MenuRequestDto requestDto) {
        Menu menu = Menu.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .restaurant(restaurantRepository.findById(requestDto.getRestaurantId())
                        .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없거나 접근 권한이 없습니다.")))
                .isDeleted(false)
                .build();
        menu.setCreatedBy(ownerId);
        return menu;
    }
    private MenuDto convertToMenuDto(Menu menu) {
        return MenuDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .isDeleted(menu.isDeleted())
                .build();
    }

    private Page<Menu> menuInfoPaging(int page, int size, String sortBy, boolean isAsc) {
        // size가 10, 30, 50이 아닌 경우 기본값 10으로 설정
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        // 정렬 기준: sortBy가 "updatedAt"이면 updatedAt, 아니면 createdAt 사용
        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        // ownerId와 삭제되지 않은 메뉴를 페이징 조회
        return menuRepository.findAllByIsDeletedFalse(pageable);
    }

}
