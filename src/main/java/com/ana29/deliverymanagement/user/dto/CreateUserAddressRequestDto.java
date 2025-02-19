package com.ana29.deliverymanagement.user.dto;

import jakarta.validation.constraints.NotNull;

public record CreateUserAddressRequestDto (@NotNull String address){
}