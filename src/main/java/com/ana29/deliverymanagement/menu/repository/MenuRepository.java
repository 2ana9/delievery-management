package com.ana29.deliverymanagement.menu.repository;

import com.ana29.deliverymanagement.menu.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {
    Optional<Menu> findByIdAndOwnerIdAndIsDeletedFalse(UUID id, String ownerId);

    Page<Menu> findAllByOwnerIdAndIsDeletedFalse(String ownerId, Pageable pageable);
}
