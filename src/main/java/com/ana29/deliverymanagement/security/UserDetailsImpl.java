package com.ana29.deliverymanagement.security;

import com.ana29.deliverymanagement.user.controller.user.UserRoleEnum;
import com.ana29.deliverymanagement.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L; // ✅ 직렬화 버전 ID 추가

    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    private String email;   // ✅ 추가: 필요한 정보만 저장
    private String phone;
    private UserRoleEnum role;

    public UserDetailsImpl(User user) {
        this.username = user.getId();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().getAuthority())); // ✅ 권한 변환
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}

