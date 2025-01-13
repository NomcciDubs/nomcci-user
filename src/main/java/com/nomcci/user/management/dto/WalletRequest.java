package com.nomcci.user.management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletRequest {
    private Long userId;

    public WalletRequest(Long userId) {
        this.userId = userId;
    }
}
