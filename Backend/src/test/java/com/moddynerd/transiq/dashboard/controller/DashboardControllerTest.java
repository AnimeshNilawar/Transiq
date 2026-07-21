//package com.moddynerd.transiq.dashboard.controller;
//
//import com.moddynerd.transiq.dashboard.dto.DashboardPaymentDetailResponse;
//import com.moddynerd.transiq.dashboard.dto.DashboardPageResponse;
//import com.moddynerd.transiq.dashboard.service.CurrentJwtUserService;
//import com.moddynerd.transiq.dashboard.service.DashboardService;
//import com.moddynerd.transiq.payment.entity.Currency;
//import com.moddynerd.transiq.payment.entity.PaymentMethodType;
//import com.moddynerd.transiq.payment.entity.PaymentStatus;
//import com.moddynerd.transiq.webhook.dto.CreateWebhookRequest;
//import com.moddynerd.transiq.webhook.dto.CreateWebhookResponse;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(DashboardController.class)
//class DashboardControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private DashboardService dashboardService;
//
//    @MockitoBean
//    private CurrentJwtUserService currentJwtUserService;
//
//    private DashboardPaymentDetailResponse paymentDetail(String reference) {
//        return new DashboardPaymentDetailResponse(
//                UUID.randomUUID(),
//                reference,
//                1000L,
//                Currency.INR,
//                PaymentStatus.SUCCEEDED,
//                PaymentMethodType.CARD,
//                "user@example.com",
//                "John Doe",
//                "order-001",
//                "Test payment",
//                Instant.now(),
//                null,
//                List.of()
//        );
//    }
//
//    @Test
//    @WithMockUser(roles = "OWNER")
//    void createWebhook_returns201() throws Exception {
//        UUID id = UUID.randomUUID();
//        when(dashboardService.createWebhook(any(CreateWebhookRequest.class)))
//                .thenReturn(new CreateWebhookResponse(id, "https://example.com/webhook", "whsec_abc123"));
//
//        mockMvc.perform(post("/api/v1/dashboard/webhooks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                {"url": "https://example.com/webhook"}
//                                """))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(id.toString()))
//                .andExpect(jsonPath("$.url").value("https://example.com/webhook"))
//                .andExpect(jsonPath("$.secret").value("whsec_abc123"));
//    }
//
//    @Test
//    @WithMockUser(roles = "DEVELOPER")
//    void createWebhook_developerCanCreate() throws Exception {
//        when(dashboardService.createWebhook(any(CreateWebhookRequest.class)))
//                .thenReturn(new CreateWebhookResponse(UUID.randomUUID(), "https://example.com/webhook", "whsec_xyz"));
//
//        mockMvc.perform(post("/api/v1/dashboard/webhooks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                {"url": "https://example.com/webhook"}
//                                """))
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    @WithMockUser(roles = "OWNER")
//    void createWebhook_blankUrl_returns400() throws Exception {
//        mockMvc.perform(post("/api/v1/dashboard/webhooks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                {"url": "  "}
//                                """))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @WithMockUser(roles = "OWNER")
//    void deleteWebhook_returns204() throws Exception {
//        mockMvc.perform(delete("/api/v1/dashboard/webhooks/" + UUID.randomUUID()))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    @WithMockUser(roles = "DEVELOPER")
//    void deleteWebhook_developerCanDelete() throws Exception {
//        mockMvc.perform(delete("/api/v1/dashboard/webhooks/" + UUID.randomUUID()))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    @WithMockUser(roles = "OWNER")
//    void retryPayment_returns200() throws Exception {
//        String reference = "pay_ref_123";
//        when(dashboardService.retryPayment(reference))
//                .thenReturn(paymentDetail(reference));
//
//        mockMvc.perform(post("/api/v1/dashboard/payments/" + reference + "/retry"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.paymentReference").value(reference))
//                .andExpect(jsonPath("$.status").value("SUCCEEDED"));
//    }
//
//    @Test
//    @WithMockUser(roles = "OWNER")
//    void retryPayment_notFound_returns404() throws Exception {
//        when(dashboardService.retryPayment("nonexistent"))
//                .thenThrow(new com.moddynerd.transiq.auth.exception.ResourceNotFoundException("Payment not found"));
//
//        mockMvc.perform(post("/api/v1/dashboard/payments/nonexistent/retry"))
//                .andExpect(status().isNotFound());
//    }
//}
