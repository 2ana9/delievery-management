package com.ana29.deliverymanagement.user.constant.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRoleEnum {

    USER("ROLE_USER"),  // 사용자 권한
    OWNER("ROLE_OWNER"), // 가게주인 권한
    MANAGER("ROLE_MANAGER"),  // 관리자 권한
    MASTER("ROLE_MASTER"); // 마스터 권한


    private final String authority;

//        사용 예시
//        System.out.println(UserRoleEnum.USER.getAuthority());  // 출력: ROLE_USER
//        System.out.println(UserRoleEnum.ADMIN.getAuthority()); // 출력: ROLE_ADMIN}

}