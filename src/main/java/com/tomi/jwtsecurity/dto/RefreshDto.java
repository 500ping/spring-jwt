package com.tomi.jwtsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RefreshDto {
    private String refreshToken;
}
