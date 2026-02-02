package com.sukhoi.mail.controller;

import com.sukhoi.mail.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TestController.class)
@TestPropertySource(properties = {
    "spring.rabbitmq.host=localhost",
    "spring.rabbitmq.port=5672",
    "spring.mail.host=localhost",
    "spring.mail.port=1025"
})
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MailService mailService;

    @Test
    void test_ShouldReturnSuccessMessage() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk())
                .andExpect(content().string("Mail Service is working!"));
    }

    @Test
    void test_ShouldCallMailServiceWithCorrectParameters() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk());

        verify(mailService, times(1)).sendActivationEmail("viet@gmail", 2, "code");
    }

    @Test
    void test_ShouldRespondWithOkStatus() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk());
    }

    @Test
    void test_ShouldRespondWithTextPlain() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/plain"));
    }

    @Test
    void test_ShouldInvokeMailServiceOnce() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"));

        verify(mailService, times(1)).sendActivationEmail(anyString(), anyInt(), anyString());
    }

    @Test
    void test_ShouldNotThrowExceptionWhenMailServiceSucceeds() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk());
    }

    @Test
    void test_ShouldHandleMultipleRequests() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk());

        verify(mailService, times(3)).sendActivationEmail("viet@gmail", 2, "code");
    }

    @Test
    void test_ShouldReturnCorrectContentLength() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        String expectedResponse = "Mail Service is working!";

        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void test_ShouldHandleGetRequestOnly() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk());
    }

    @Test
    void test_ShouldUseCorrectEndpoint() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk());

        // Verify wrong endpoint returns 404
        mockMvc.perform(get("/test-mail/wrong"))
                .andExpect(status().isNotFound());
    }

    @Test
    void test_ShouldPassExactEmailToMailService() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"));

        verify(mailService).sendActivationEmail(eq("viet@gmail"), anyInt(), anyString());
    }

    @Test
    void test_ShouldPassExactUserIdToMailService() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"));

        verify(mailService).sendActivationEmail(anyString(), eq(2), anyString());
    }

    @Test
    void test_ShouldPassExactActivationCodeToMailService() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"));

        verify(mailService).sendActivationEmail(anyString(), anyInt(), eq("code"));
    }

    @Test
    void test_ShouldHandleMailServiceException() throws Exception {
        doThrow(new RuntimeException("Mail service error"))
                .when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void test_ShouldNotRequireQueryParameters() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk());

        verify(mailService, times(1)).sendActivationEmail(anyString(), anyInt(), anyString());
    }

    @Test
    void test_ShouldNotRequireRequestBody() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk());
    }

    @Test
    void test_ShouldHaveCorrectMappingPath() throws Exception {
        doNothing().when(mailService).sendActivationEmail(anyString(), anyInt(), anyString());

        // Should work with /test-mail
        mockMvc.perform(get("/test-mail"))
                .andExpect(status().isOk());

        // Should not work with root path
        mockMvc.perform(get("/"))
                .andExpect(status().isNotFound());
    }
}