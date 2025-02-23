package com.ana29.deliverymanagement.area.repository;

import com.ana29.deliverymanagement.area.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, Long>, AreaRepositoryCustom {

}
