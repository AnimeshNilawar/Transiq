package com.moddynerd.transiq.payment.repository;

import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.payment.entity.Payment;
import com.moddynerd.transiq.payment.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByPaymentReference(String paymentReference);

    Optional<Payment> findByMerchantAndIdempotencyKey(
            Merchant merchant,
            String idempotencyKey
    );

    Optional<Payment> findByMerchantAndPaymentReference(
            Merchant merchant,
            String paymentReference
    );

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
    Long sumAmountByStatus(PaymentStatus status);

    List<Payment> findTop10ByOrderByCreatedAtDesc();

    long countByMerchant(Merchant merchant);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.merchant = :merchant AND p.status = :status")
    long sumAmountByMerchantAndStatus(Merchant merchant, PaymentStatus status);

    @Query(value = """
            SELECT CAST(p.created_at AS date) AS date,
                   COALESCE(SUM(p.amount), 0) AS volume,
                   COUNT(*) AS count
            FROM payments p
            WHERE p.status = 'SUCCEEDED'
              AND p.created_at >= :since
            GROUP BY CAST(p.created_at AS date)
            ORDER BY CAST(p.created_at AS date)
            """, nativeQuery = true)
    List<Object[]> revenueTimeSeries(java.time.Instant since);

    @Query(value = """
            SELECT CAST(p.created_at AS date) AS date,
                   COALESCE(SUM(CASE WHEN p.status = 'SUCCEEDED' THEN 1 ELSE 0 END), 0) AS succeeded,
                   COALESCE(SUM(CASE WHEN p.status = 'FAILED' THEN 1 ELSE 0 END), 0) AS failed
            FROM payments p
            WHERE p.created_at >= :since
            GROUP BY CAST(p.created_at AS date)
            ORDER BY CAST(p.created_at AS date)
            """, nativeQuery = true)
    List<Object[]> failureTrend(java.time.Instant since);

    @Query(value = """
            SELECT p.merchant_id, m.business_name,
                   COUNT(*) AS total,
                   SUM(CASE WHEN p.status = 'FAILED' THEN 1 ELSE 0 END) AS failed_count
            FROM payments p
            JOIN merchants m ON m.id = p.merchant_id
            WHERE p.created_at >= :since
            GROUP BY p.merchant_id, m.business_name
            HAVING SUM(CASE WHEN p.status = 'FAILED' THEN 1 ELSE 0 END) > 0
               AND SUM(CASE WHEN p.status = 'FAILED' THEN 1 ELSE 0 END) * 1.0 / COUNT(*) > 0.3
            ORDER BY failed_count DESC
            """, nativeQuery = true)
    List<Object[]> suspiciousActivity(java.time.Instant since);
}