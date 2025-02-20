package com.ana29.deliverymanagement.user.dto;

import com.ana29.deliverymanagement.user.entity.User;
import lombok.Getter;
import java.io.Serializable;

@Getter
public class UserDetailsDto implements Serializable { // ✅ Redis에 저장 가능
    private String id;
    private String nickname;
    private String email;
    private String phone;

    public UserDetailsDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.phone = user.getPhone();
    }
}
