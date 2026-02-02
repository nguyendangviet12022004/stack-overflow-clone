package com.sukhoi.mail.message;

import com.sukhoi.mail.dto.message.ActivateAccountRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class MessageReceiverTest {

    @InjectMocks
    private MessageReceiver messageReceiver;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void processOrder_ShouldHandleValidRequest(CapturedOutput output) {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@example.com")
                .userId(123)
                .activationCode("ABC123")
                .build();
        String routingKey = "r.mail.active.account";

        messageReceiver.processOrder(request, routingKey);

        String capturedOutput = output.getOut();
        assertThat(capturedOutput).contains("test@example.com");
        assertThat(capturedOutput).contains("123");
        assertThat(capturedOutput).contains("ABC123");
        assertThat(capturedOutput).contains("Routing Key: " + routingKey);
    }

    @Test
    void processOrder_ShouldPrintRoutingKey(CapturedOutput output) {
        ActivateAccountRequest request = new ActivateAccountRequest("user@test.com", 456, "XYZ789");
        String routingKey = "test.routing.key";

        messageReceiver.processOrder(request, routingKey);

        String capturedOutput = output.getOut();
        assertThat(capturedOutput).contains("Routing Key: " + routingKey);
    }

    @Test
    void processOrder_ShouldHandleNullEmail(CapturedOutput output) {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email(null)
                .userId(123)
                .activationCode("CODE")
                .build();
        String routingKey = "r.mail.active.account";

        assertDoesNotThrow(() -> messageReceiver.processOrder(request, routingKey));
    }

    @Test
    void processOrder_ShouldHandleEmptyEmail(CapturedOutput output) {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("")
                .userId(123)
                .activationCode("CODE")
                .build();
        String routingKey = "r.mail.active.account";

        messageReceiver.processOrder(request, routingKey);

        String capturedOutput = output.getOut();
        assertThat(capturedOutput).contains("Routing Key: " + routingKey);
    }

    @Test
    void processOrder_ShouldHandleZeroUserId(CapturedOutput output) {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@test.com")
                .userId(0)
                .activationCode("CODE")
                .build();
        String routingKey = "r.mail.active.account";

        messageReceiver.processOrder(request, routingKey);

        String capturedOutput = output.getOut();
        assertThat(capturedOutput).contains("userId=0");
    }

    @Test
    void processOrder_ShouldHandleNegativeUserId(CapturedOutput output) {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@test.com")
                .userId(-1)
                .activationCode("CODE")
                .build();
        String routingKey = "r.mail.active.account";

        messageReceiver.processOrder(request, routingKey);

        String capturedOutput = output.getOut();
        assertThat(capturedOutput).contains("userId=-1");
    }

    @Test
    void processOrder_ShouldHandleNullActivationCode(CapturedOutput output) {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@test.com")
                .userId(123)
                .activationCode(null)
                .build();
        String routingKey = "r.mail.active.account";

        assertDoesNotThrow(() -> messageReceiver.processOrder(request, routingKey));
    }

    @Test
    void processOrder_ShouldHandleEmptyActivationCode(CapturedOutput output) {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@test.com")
                .userId(123)
                .activationCode("")
                .build();
        String routingKey = "r.mail.active.account";

        messageReceiver.processOrder(request, routingKey);

        String capturedOutput = output.getOut();
        assertThat(capturedOutput).contains("Routing Key: " + routingKey);
    }

    @Test
    void processOrder_ShouldHandleEmptyRoutingKey(CapturedOutput output) {
        ActivateAccountRequest request = new ActivateAccountRequest("test@test.com", 123, "CODE");
        String routingKey = "";

        messageReceiver.processOrder(request, routingKey);

        String capturedOutput = output.getOut();
        assertThat(capturedOutput).contains("Routing Key: ");
    }

    @Test
    void processOrder_ShouldHandleNullRoutingKey(CapturedOutput output) {
        ActivateAccountRequest request = new ActivateAccountRequest("test@test.com", 123, "CODE");

        assertDoesNotThrow(() -> messageReceiver.processOrder(request, null));
    }

    @Test
    void processOrder_ShouldHandleSpecialCharactersInEmail(CapturedOutput output) {
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test+special@example.com")
                .userId(123)
                .activationCode("CODE")
                .build();
        String routingKey = "r.mail.active.account";

        messageReceiver.processOrder(request, routingKey);

        String capturedOutput = output.getOut();
        assertThat(capturedOutput).contains("test+special@example.com");
    }

    @Test
    void processOrder_ShouldHandleLongActivationCode(CapturedOutput output) {
        String longCode = "A".repeat(1000);
        ActivateAccountRequest request = ActivateAccountRequest.builder()
                .email("test@test.com")
                .userId(123)
                .activationCode(longCode)
                .build();
        String routingKey = "r.mail.active.account";

        messageReceiver.processOrder(request, routingKey);

        String capturedOutput = output.getOut();
        assertThat(capturedOutput).contains("Routing Key: " + routingKey);
    }

    @Test
    void processOrder_ShouldHandleComplexRoutingKey(CapturedOutput output) {
        ActivateAccountRequest request = new ActivateAccountRequest("test@test.com", 123, "CODE");
        String routingKey = "r.mail.active.account.priority.high";

        messageReceiver.processOrder(request, routingKey);

        String capturedOutput = output.getOut();
        assertThat(capturedOutput).contains("Routing Key: " + routingKey);
    }

    @Test
    void processOrder_ShouldHandleMultipleInvocations(CapturedOutput output) {
        ActivateAccountRequest request1 = new ActivateAccountRequest("user1@test.com", 1, "CODE1");
        ActivateAccountRequest request2 = new ActivateAccountRequest("user2@test.com", 2, "CODE2");
        String routingKey = "r.mail.active.account";

        messageReceiver.processOrder(request1, routingKey);
        messageReceiver.processOrder(request2, routingKey);

        String capturedOutput = output.getOut();
        assertThat(capturedOutput).contains("user1@test.com");
        assertThat(capturedOutput).contains("user2@test.com");
    }
}