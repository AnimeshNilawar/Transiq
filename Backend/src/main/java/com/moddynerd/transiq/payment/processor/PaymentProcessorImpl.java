package com.moddynerd.transiq.payment.processor;

import com.moddynerd.transiq.event.payment.PaymentSucceededEvent;
import com.moddynerd.transiq.event.publisher.DomainEventPublisher;
import com.moddynerd.transiq.payment.attempt.entity.FailureCode;
import com.moddynerd.transiq.payment.attempt.entity.PaymentAttempt;
import com.moddynerd.transiq.payment.attempt.service.PaymentAttemptService;
import com.moddynerd.transiq.payment.authorization.AuthorizationDecision;
import com.moddynerd.transiq.payment.authorization.AuthorizationResult;
import com.moddynerd.transiq.payment.entity.CardPaymentDetails;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentMethodType;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import com.moddynerd.transiq.payment.entity.UpiPaymentDetails;
import com.moddynerd.transiq.payment.gateway.authorization.GatewayAuthorizationService;
import com.moddynerd.transiq.payment.gateway.bin.BinRecord;
import com.moddynerd.transiq.payment.gateway.bin.BinResolver;
import com.moddynerd.transiq.payment.gateway.card.Card;
import com.moddynerd.transiq.payment.gateway.metadata.CardMetadata;
import com.moddynerd.transiq.payment.gateway.metadata.CardMetadataFactory;
import com.moddynerd.transiq.payment.gateway.model.*;
import com.moddynerd.transiq.payment.gateway.payment.PaymentHandler;
import com.moddynerd.transiq.payment.repository.CardPaymentDetailsRepository;
import com.moddynerd.transiq.payment.repository.PaymentRepository;
import com.moddynerd.transiq.payment.repository.UpiPaymentDetailsRepository;
import com.moddynerd.transiq.payment.state.PaymentStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentProcessorImpl implements PaymentProcessor {

    private final PaymentAttemptService paymentAttemptService;
    private final PaymentStateMachine paymentStateMachine;
    private final PaymentRepository paymentRepository;
    private final GatewayAuthorizationService gatewayAuthorizationService;
    private final CardPaymentDetailsRepository cardPaymentDetailsRepository;
    private final UpiPaymentDetailsRepository upiPaymentDetailsRepository;
    private final BinResolver binResolver;
    private final CardMetadataFactory cardMetadataFactory;
    private final DomainEventPublisher domainEventPublisher;
    private final List<PaymentHandler> paymentHandlers;

    @Override
    public void process(Payment payment) {

        PaymentAttempt attempt =
                paymentAttemptService.createAttempt(payment);

        paymentAttemptService.markProcessing(attempt);

        paymentStateMachine.transition(
                payment,
                PaymentStatus.PROCESSING
        );

        paymentRepository.save(payment);

        AuthorizationResult result = authorize(payment);

        if (result.decision() == AuthorizationDecision.APPROVED) {

            paymentAttemptService.markSucceeded(attempt);

            paymentStateMachine.transition(
                    payment,
                    PaymentStatus.SUCCEEDED
            );

        } else {

            paymentAttemptService.markFailed(
                    attempt,
                    result.failureCode(),
                    result.message()
            );

            paymentStateMachine.transition(
                    payment,
                    PaymentStatus.FAILED
            );
        }

        paymentRepository.save(payment);

        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            domainEventPublisher.publish(
                    new PaymentSucceededEvent(
                            payment.getMerchant().getId(),
                            payment.getId(),
                            payment.getPaymentReference()
                    )
            );
        }
    }

    private AuthorizationResult authorize(Payment payment) {

        if (payment.getPaymentMethodType() != PaymentMethodType.CARD) {
            Optional<PaymentHandler> handler = paymentHandlers.stream()
                    .filter(h -> h.supports(payment.getPaymentMethodType()))
                    .findFirst();

            if (handler.isPresent()) {
                AuthorizationRequest request = new AuthorizationRequest(
                        payment.getId(),
                        payment.getPaymentReference(),
                        payment.getMerchant().getId(),
                        payment.getAmount(),
                        payment.getCurrency(),
                        payment.getPaymentMethodType(),
                        null,
                        null,
                        Instant.now()
                );

                AuthorizationResponse response = handler.get().authorize(request);

                if (payment.getPaymentMethodType() == PaymentMethodType.UPI) {
                    updateUpiDetails(payment, response);
                }

                return mapToResult(response);
            }

            return new AuthorizationResult(
                    AuthorizationDecision.APPROVED,
                    FailureCode.NONE,
                    "Payment Approved"
            );
        }

        Optional<CardPaymentDetails> optionalDetails =
                cardPaymentDetailsRepository.findByPaymentId(payment.getId());

        if (optionalDetails.isEmpty()) {
            return new AuthorizationResult(
                    AuthorizationDecision.APPROVED,
                    FailureCode.NONE,
                    "Payment Approved"
            );
        }

        CardPaymentDetails details = optionalDetails.get();

        Card card = buildCard(details);

        BinRecord binRecord = binResolver.resolve(card);

        CardMetadata metadata = cardMetadataFactory.from(binRecord);

        AuthorizationRequest request = new AuthorizationRequest(
                payment.getId(),
                payment.getPaymentReference(),
                payment.getMerchant().getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethodType(),
                card,
                metadata,
                Instant.now()
        );

        AuthorizationResponse response =
                gatewayAuthorizationService.authorize(request);

        updateCardDetails(details, response);

        return mapToResult(response);
    }

    private Card buildCard(CardPaymentDetails details) {
        return new Card(
                null,
                details.getMaskedCardNumber(),
                details.getMaskedCardNumber() != null
                        ? details.getMaskedCardNumber().substring(0, 6)
                        : null,
                details.getMaskedCardNumber() != null
                        ? details.getMaskedCardNumber().substring(
                        details.getMaskedCardNumber().length() - 4
                )
                        : null,
                null,
                details.getCardNetwork() != null
                        ? mapCardBrand(details.getCardNetwork())
                        : null,
                null,
                "IN"
        );
    }

    private com.moddynerd.transiq.payment.gateway.card.CardBrand mapCardBrand(
            CardNetwork network
    ) {
        return switch (network) {
            case VISA -> com.moddynerd.transiq.payment.gateway.card.CardBrand.VISA;
            case MASTERCARD -> com.moddynerd.transiq.payment.gateway.card.CardBrand.MASTERCARD;
            case RUPAY -> com.moddynerd.transiq.payment.gateway.card.CardBrand.RUPAY;
        };
    }

    private void updateUpiDetails(
            Payment payment,
            AuthorizationResponse response
    ) {
        upiPaymentDetailsRepository.findByPaymentId(payment.getId())
                .ifPresent(details -> {
                    if (response.metadata() != null) {
                        details.setUpiTransactionReference(
                                response.metadata().authorizationCode()
                        );
                    }
                    details.setGatewayResponseCode(
                            response.responseCode().name()
                    );
                    details.setGatewayMessage(response.message());
                    upiPaymentDetailsRepository.save(details);
                });
    }

    private void updateCardDetails(
            CardPaymentDetails details,
            AuthorizationResponse response
    ) {
        if (response.metadata() != null) {
            details.setAuthorizationCode(
                    response.metadata().authorizationCode()
            );
        }
        details.setGatewayResponseCode(
                response.responseCode().name()
        );
        details.setGatewayMessage(response.message());
        cardPaymentDetailsRepository.save(details);
    }

    private AuthorizationResult mapToResult(AuthorizationResponse response) {
        return new AuthorizationResult(
                mapDecision(response.decision()),
                mapFailureCode(response.failureCode()),
                response.message()
        );
    }

    private AuthorizationDecision mapDecision(
            com.moddynerd.transiq.payment.gateway.model.AuthorizationDecision decision
    ) {
        return switch (decision) {
            case APPROVED -> AuthorizationDecision.APPROVED;
            case DECLINED -> AuthorizationDecision.DECLINED;
        };
    }

    private FailureCode mapFailureCode(AuthorizationFailureCode failureCode) {
        return switch (failureCode) {
            case NONE -> FailureCode.NONE;
            case INSUFFICIENT_FUNDS -> FailureCode.INSUFFICIENT_FUNDS;
            case CARD_EXPIRED -> FailureCode.INVALID_PAYMENT_METHOD;
            case CARD_BLOCKED -> FailureCode.BANK_DECLINED;
            case CVV_INVALID -> FailureCode.INVALID_PAYMENT_METHOD;
            case NETWORK_TIMEOUT -> FailureCode.TIMEOUT;
            case BANK_UNAVAILABLE -> FailureCode.BANK_DECLINED;
            case LIMIT_EXCEEDED -> FailureCode.BANK_DECLINED;
            case SUSPECTED_FRAUD -> FailureCode.BANK_DECLINED;
            case UNKNOWN -> FailureCode.UNKNOWN;
        };
    }

}
