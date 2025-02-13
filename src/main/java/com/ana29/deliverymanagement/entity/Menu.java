package com.ana29.deliverymanagement.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_menus")
public class Menu extends Timestamped  {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "menu_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "content", nullable = false, length = 100)
    private String content;

    @Column(name = "retaurant_id", nullable = false)
    private Long retaurant_id;


}
