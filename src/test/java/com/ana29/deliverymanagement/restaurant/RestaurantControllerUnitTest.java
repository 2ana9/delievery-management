package com.ana29.deliverymanagement.restaurant;

import com.ana29.deliverymanagement.restaurant.controller.RestaurantController;
import com.ana29.deliverymanagement.restaurant.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantService restaurantService;

    @Test
    void testSearchRestaurants() {

    }

    @Test
    void testDeleteRestaurant() {

    }



}