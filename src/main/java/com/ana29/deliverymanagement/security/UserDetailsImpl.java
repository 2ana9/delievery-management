package com.ana29.deliverymanagement.security;

import com.ana29.deliverymanagement.user.controller.user.UserRoleEnum;
import com.ana29.deliverymanagement.user.entity.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 🔹 Jackson 역직렬화 오류 방지
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

    // 🔹 권한을 `String`으로 저장하여 Redis에서 직렬화 오류 방지
    private List<String> authorities;

    /**
     * 🔹 `User` 엔티티를 기반으로 UserDetailsImpl 생성
     */
    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.enabled = true;

        // 🔹 `GrantedAuthority` -> `String`으로 변환하여 Redis에 저장
        this.authorities = List.of(user.getRole().getAuthority());
    }

    /**
     * 🔹 JSON 역직렬화 지원 (Redis에서 불러올 때 사용)
     */
    @JsonCreator
    public UserDetailsImpl(
            @JsonProperty("id") String id,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("password") String password,
            @JsonProperty("email") String email,
            @JsonProperty("phone") String phone,
            @JsonProperty("enabled") boolean enabled,
            @JsonProperty("authorities") List<String> authorities) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    /**
     * 🔹 `List<String>` -> `List<GrantedAuthority>` 변환
     */
    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new) // 🔹 역직렬화 시 `SimpleGrantedAuthority`로 변환
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() { return id; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}
