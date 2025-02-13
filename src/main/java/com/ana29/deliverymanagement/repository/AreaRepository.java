package com.ana29.deliverymanagement.repository;

import com.ana29.deliverymanagement.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AreaRepository extends JpaRepository<Area, UUID> {
}
