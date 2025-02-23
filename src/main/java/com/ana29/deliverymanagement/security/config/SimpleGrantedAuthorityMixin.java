package com.ana29.deliverymanagement.security.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public abstract class SimpleGrantedAuthorityMixin {
    // 구현 없이 애너테이션만 제공
}