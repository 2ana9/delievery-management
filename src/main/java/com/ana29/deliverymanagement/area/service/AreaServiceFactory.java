package com.ana29.deliverymanagement.area.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AreaServiceFactory {
    private final Map<String, AreaServiceInterface> areaServiceMap;

    public AreaServiceFactory(Map<String, AreaServiceInterface> areaServiceMap) {
        this.areaServiceMap = areaServiceMap;
    }

    public AreaServiceInterface getService(String type) {
        return areaServiceMap.getOrDefault(type.toLowerCase(), areaServiceMap.get("postgres"));
    }
}
