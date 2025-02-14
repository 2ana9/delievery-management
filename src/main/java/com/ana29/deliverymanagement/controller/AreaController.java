package com.ana29.deliverymanagement.controller;


import com.ana29.deliverymanagement.constant.UserRoleEnum;
import com.ana29.deliverymanagement.dto.AreaRequestDto;
import com.ana29.deliverymanagement.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import security.UserDetailsImpl;

import static com.ana29.deliverymanagement.constant.UserRoleEnum.ADMIN;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @PostMapping
    public String createArea(@RequestBody AreaRequestDto request, @AuthenticationPrincipal UserDetailsImpl userDetails) {

//        String username = userDetails.getUser().getUsername();
//        UserRoleEnum role = userDetails.getUser().getRole();
        UserRoleEnum role = UserRoleEnum.ADMIN;

        if (!ADMIN.equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. User role is not MANAGER.");
        }

        return areaService.createArea(request, userDetails);
    }
}
