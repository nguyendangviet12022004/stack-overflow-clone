package com.sukhoi.mail.dto.message;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ActivateAccountRequestTest {

    @Test
    void builder_ShouldCreateInstanceWithAllFields() {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@example.com")
                .userId(123)
                .activationCode("ABC123")
                .build();

        assertThat(request).isNotNull();
        assertThat(request.getEmail()).isEqualTo("test@example.com");
        assertThat(request.getUserId()).isEqualTo(123);
        assertThat(request.getActivationCode()).isEqualTo("ABC123");
    }

    @Test
    void noArgsConstructor_ShouldCreateInstance() {
        ActivateAccountRequest request = new ActivateAccountRequest();

        assertThat(request).isNotNull();
        assertThat(request.getEmail()).isNull();
        assertThat(request.getUserId()).isEqualTo(0);
        assertThat(request.getActivationCode()).isNull();
    }

    @Test
    void allArgsConstructor_ShouldCreateInstanceWithAllFields() {
        ActivateAccountRequest request = new ActivateAccountRequest(
                "user@test.com",
                456,
                "XYZ789"
        );

        assertThat(request).isNotNull();
        assertThat(request.getEmail()).isEqualTo("user@test.com");
        assertThat(request.getUserId()).isEqualTo(456);
        assertThat(request.getActivationCode()).isEqualTo("XYZ789");
    }

    @Test
    void setters_ShouldUpdateFields() {
        ActivateAccountRequest request = new ActivateAccountRequest();
        request.setEmail("new@example.com");
        request.setUserId(789);
        request.setActivationCode("CODE456");

        assertThat(request.getEmail()).isEqualTo("new@example.com");
        assertThat(request.getUserId()).isEqualTo(789);
        assertThat(request.getActivationCode()).isEqualTo("CODE456");
    }

    @Test
    void equals_ShouldReturnTrueForSameContent() {
        ActivateAccountRequest request1 = new ActivateAccountRequest("test@test.com", 100, "CODE");
        ActivateAccountRequest request2 = new ActivateAccountRequest("test@test.com", 100, "CODE");

        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalseForDifferentContent() {
        ActivateAccountRequest request1 = new ActivateAccountRequest("test@test.com", 100, "CODE");
        ActivateAccountRequest request2 = new ActivateAccountRequest("different@test.com", 100, "CODE");

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    void toString_ShouldContainAllFields() {
        ActivateAccountRequest request = new ActivateAccountRequest(
                "test@example.com",
                123,
                "ABC123"
        );

        String result = request.toString();

        assertThat(result).contains("test@example.com");
        assertThat(result).contains("123");
        assertThat(result).contains("ABC123");
    }

    @Test
    void builder_ShouldHandleNullEmail() {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email(null)
                .userId(123)
                .activationCode("CODE")
                .build();

        assertThat(request.getEmail()).isNull();
        assertThat(request.getUserId()).isEqualTo(123);
        assertThat(request.getActivationCode()).isEqualTo("CODE");
    }

    @Test
    void builder_ShouldHandleNullActivationCode() {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@test.com")
                .userId(123)
                .activationCode(null)
                .build();

        assertThat(request.getEmail()).isEqualTo("test@test.com");
        assertThat(request.getUserId()).isEqualTo(123);
        assertThat(request.getActivationCode()).isNull();
    }

    @Test
    void builder_ShouldHandleZeroUserId() {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@test.com")
                .userId(0)
                .activationCode("CODE")
                .build();

        assertThat(request.getUserId()).isEqualTo(0);
    }

    @Test
    void builder_ShouldHandleNegativeUserId() {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@test.com")
                .userId(-1)
                .activationCode("CODE")
                .build();

        assertThat(request.getUserId()).isEqualTo(-1);
    }

    @Test
    void builder_ShouldHandleEmptyEmail() {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("")
                .userId(123)
                .activationCode("CODE")
                .build();

        assertThat(request.getEmail()).isEmpty();
    }

    @Test
    void builder_ShouldHandleEmptyActivationCode() {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@test.com")
                .userId(123)
                .activationCode("")
                .build();

        assertThat(request.getActivationCode()).isEmpty();
    }

    @Test
    void builder_ShouldHandleLargeUserId() {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@test.com")
                .userId(Integer.MAX_VALUE)
                .activationCode("CODE")
                .build();

        assertThat(request.getUserId()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void builder_ShouldHandleSpecialCharactersInEmail() {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test+special@example.com")
                .userId(123)
                .activationCode("CODE")
                .build();

        assertThat(request.getEmail()).isEqualTo("test+special@example.com");
    }

    @Test
    void builder_ShouldHandleLongActivationCode() {
        String longCode = "A".repeat(1000);
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@test.com")
                .userId(123)
                .activationCode(longCode)
                .build();

        assertThat(request.getActivationCode()).hasSize(1000);
        assertThat(request.getActivationCode()).isEqualTo(longCode);
    }
}