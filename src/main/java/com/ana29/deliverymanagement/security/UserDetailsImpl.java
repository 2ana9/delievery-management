package com.ana29.deliverymanagement.security;

import com.ana29.deliverymanagement.security.config.AuthorityDeserializer;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.ana29.deliverymanagement.user.entity.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailsImpl implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String nickname;
    private String password;
    private String email;
    private String phone;
    private UserRoleEnum role;
    private boolean enabled;

    // authorities를 단순 문자열 리스트로 저장 (예: ["ROLE_OWNER"])
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonDeserialize(contentUsing = AuthorityDeserializer.class)
    private List<String> authorities;

    /**
     * User 엔티티 기반 생성자
     */
    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.enabled = true;
        this.authorities = List.of(user.getRole().getAuthority());
    }

    /**
     * 문자열 리스트를 GrantedAuthority 리스트로 변환하여 반환
     */
    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    @Override public String getUsername() { return id; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}
