package com.moddynerd.transiq.webhook;

import com.moddynerd.transiq.event.payment.PaymentSucceededEvent;
import com.moddynerd.transiq.event.refund.RefundSucceededEvent;
import com.moddynerd.transiq.event.settlement.SettlementCompletedEvent;
import com.moddynerd.transiq.webhook.dispatcher.WebhookDispatcher;
import com.moddynerd.transiq.webhook.dto.WebhookPayload;
import com.moddynerd.transiq.webhook.listener.WebhookEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookEventListenerTest {

    @Mock
    private WebhookDispatcher webhookDispatcher;

    @InjectMocks
    private WebhookEventListener webhookEventListener;

    private final UUID merchantId = UUID.randomUUID();
    private final UUID paymentId = UUID.randomUUID();
    private final UUID refundId = UUID.randomUUID();
    private final UUID settlementId = UUID.randomUUID();

    @Test
    void handlePaymentSucceeded_dispatchesWebhook() {
        PaymentSucceededEvent event = new PaymentSucceededEvent(
                merchantId, paymentId, "pay_ref_123"
        );

        webhookEventListener.handlePaymentSucceeded(event);

        ArgumentCaptor<WebhookPayload> payloadCaptor = ArgumentCaptor.forClass(WebhookPayload.class);
        verify(webhookDispatcher).dispatch(payloadCaptor.capture(), eq(merchantId));

        WebhookPayload payload = payloadCaptor.getValue();
        assertThat(payload.type()).isEqualTo("payment.succeeded");
        assertThat(payload.reference()).isEqualTo("pay_ref_123");
        assertThat(payload.data()).isEqualTo(event);
        assertThat(payload.id()).isNotNull();
        assertThat(payload.occurredAt()).isNotNull();
    }

    @Test
    void handleRefundSucceeded_dispatchesWebhook() {
        RefundSucceededEvent event = new RefundSucceededEvent(
                merchantId, refundId, "ref_456"
        );

        webhookEventListener.handleRefundSucceeded(event);

        ArgumentCaptor<WebhookPayload> payloadCaptor = ArgumentCaptor.forClass(WebhookPayload.class);
        verify(webhookDispatcher).dispatch(payloadCaptor.capture(), eq(merchantId));

        WebhookPayload payload = payloadCaptor.getValue();
        assertThat(payload.type()).isEqualTo("refund.succeeded");
        assertThat(payload.reference()).isEqualTo("ref_456");
        assertThat(payload.data()).isEqualTo(event);
        assertThat(payload.id()).isNotNull();
    }

    @Test
    void handleSettlementCompleted_dispatchesWebhook() {
        SettlementCompletedEvent event = new SettlementCompletedEvent(
                merchantId, settlementId, "set_789"
        );

        webhookEventListener.handleSettlementCompleted(event);

        ArgumentCaptor<WebhookPayload> payloadCaptor = ArgumentCaptor.forClass(WebhookPayload.class);
        verify(webhookDispatcher).dispatch(payloadCaptor.capture(), eq(merchantId));

        WebhookPayload payload = payloadCaptor.getValue();
        assertThat(payload.type()).isEqualTo("settlement.completed");
        assertThat(payload.reference()).isEqualTo("set_789");
        assertThat(payload.data()).isEqualTo(event);
        assertThat(payload.id()).isNotNull();
    }

    @Test
    void handlePaymentSucceeded_eachPayloadGetsUniqueId() {
        PaymentSucceededEvent event1 = new PaymentSucceededEvent(
                merchantId, paymentId, "pay_ref_1"
        );
        PaymentSucceededEvent event2 = new PaymentSucceededEvent(
                merchantId, paymentId, "pay_ref_2"
        );

        webhookEventListener.handlePaymentSucceeded(event1);
        webhookEventListener.handlePaymentSucceeded(event2);

        ArgumentCaptor<WebhookPayload> captor = ArgumentCaptor.forClass(WebhookPayload.class);
        verify(webhookDispatcher, times(2)).dispatch(captor.capture(), eq(merchantId));

        assertThat(captor.getAllValues().get(0).id())
                .isNotEqualTo(captor.getAllValues().get(1).id());
    }
}
