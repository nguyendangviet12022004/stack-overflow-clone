package com.sukhoi.user.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivateAccountRequest {
    private String email;

    private String userId;

    private String activationCode;
}
