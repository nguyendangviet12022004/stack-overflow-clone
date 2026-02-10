package com.sukhoi.mail.dto.message;

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

    private int userId;

    private String activationCode;
}
