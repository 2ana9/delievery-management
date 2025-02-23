package com.ana29.deliverymanagement.restaurant.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuUpdateRequestDto {

    @NotNull(message = "메뉴 이름은 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s]{1,20}$", message = "메뉴 이름은 특수문자를 제외한 20자 이하여야 합니다.")
    private String name;

    @NotNull(message = "메뉴 가격은 필수입니다.")
    @Min(value = 0, message = "메뉴 가격은 0 이상이어야 합니다.")
    @Max(value = 2000000, message = "메뉴 가격은 200만원 이하이어야 합니다.")
    private Long price;

    private String description;
}
