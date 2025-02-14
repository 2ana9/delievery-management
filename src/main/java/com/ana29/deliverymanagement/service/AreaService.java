//package com.ana29.deliverymanagement.service;
//
//import com.ana29.deliverymanagement.dto.AreaRequestDto;
//import com.ana29.deliverymanagement.entity.Area;
//import com.ana29.deliverymanagement.repository.AreaRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import com.ana29.deliverymanagement.security.UserDetailsImpl;
//
//@Service
//@RequiredArgsConstructor
//public class AreaService {
//
//    private final AreaRepository areaRepository;
//
//    public String createArea(AreaRequestDto request, UserDetailsImpl userDetails) {
//        String username = "관리자1";
//
//        Area area = Area.builder()
//                .code(request.getCode())
//                .cityName(request.getCityName())
//                .parentId(null)
//                .build();
//
//        areaRepository.save(area);
//
//        return "";
//    }
//}
