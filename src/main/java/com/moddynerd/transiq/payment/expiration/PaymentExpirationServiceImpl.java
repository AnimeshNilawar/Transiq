package com.moddynerd.transiq.payment.expiration;

import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import com.moddynerd.transiq.payment.repository.PaymentRepository;
import com.moddynerd.transiq.payment.state.PaymentStateMachine;
import com.moddynerd.transiq.shared.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentExpirationServiceImpl implements PaymentExpirationService{
    private final PaymentRepository paymentRepository;
    private final PaymentStateMachine paymentStateMachine;

    @Override
    public void validate(Payment payment) {
        if (payment.getStatus() == PaymentStatus.EXPIRED) {
            throw new ConflictException("Payment has expired");
        }

        if(Instant.now().isAfter(payment.getExpiresAt())){
            paymentStateMachine.transition(
                    payment,
                    PaymentStatus.EXPIRED
            );

            paymentRepository.save(payment);

            throw new ConflictException("Payment has expired");
        }
    }
}
