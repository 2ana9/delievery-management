package com.ana29.deliverymanagement.user.entity;

import com.ana29.deliverymanagement.global.entity.Timestamped;
import com.ana29.deliverymanagement.user.dto.UpdateUserAddressRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name ="p_useraddress")
public class UserAddress extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "useraddress_id", columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String address;

    @Column(length = 50)
    private String detail;

    @Column(nullable = false)
    @Builder.Default
    private Boolean defaultAddress = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public boolean updateAddress(UpdateUserAddressRequestDto requestDto) {
        String address = requestDto.address();
        String detail = requestDto.detail();

        boolean isChanged = false;

        if (!this.address.equals(address)) {
            this.address = address;
            isChanged = true;
        }
        if (!this.detail.equals(detail)) {
            this.detail = detail;
            isChanged = true;
        }

        return isChanged;
    }

    public void updateDefaultAddress(boolean isDefault) {
        this.defaultAddress = isDefault;
    }

    public void delete(String deletedBy){
        super.delete(deletedBy);
        this.isDeleted = true;
    }
}
