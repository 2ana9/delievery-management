package com.ana29.deliverymanagement.security;

import com.ana29.deliverymanagement.security.config.AuthorityDeserializer;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.ana29.deliverymanagement.user.entity.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Builder
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

    // authorities를 List<String>으로 유지하고, 각 요소에 대해 AuthorityDeserializer 적용
    @JsonDeserialize(contentUsing = AuthorityDeserializer.class)
    private List<String> authorities;

    /**
     * User 엔티티를 기반으로 생성
     */
    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.enabled = true;
        // 저장할 때는 단일 문자열로 저장 (예: "ROLE_MASTER")
        this.authorities = List.of(user.getRole().getAuthority());
    }

    /**
     * JSON 역직렬화 지원 생성자
     */
    @JsonCreator
    public UserDetailsImpl(
            @JsonProperty("id") String id,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("password") String password,
            @JsonProperty("email") String email,
            @JsonProperty("phone") String phone,
            @JsonProperty("role") UserRoleEnum role,
            @JsonProperty("enabled") boolean enabled,
            @JsonProperty("authorities") List<String> authorities) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.enabled = enabled;
        this.authorities = authorities != null ? authorities : Collections.emptyList();
    }

    public UserDetailsImpl(String username, List<GrantedAuthority> authorities) {
        this.id = username;
        this.enabled = true;
        this.authorities = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    /**
     * List<String> → List<GrantedAuthority> 변환
     */
    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
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
