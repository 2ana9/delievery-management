package com.ana29.deliverymanagement.repository;

import com.ana29.deliverymanagement.entity.Menu;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, UUID> {

}
