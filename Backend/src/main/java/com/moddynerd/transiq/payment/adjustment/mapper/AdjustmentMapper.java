package com.moddynerd.transiq.payment.adjustment.mapper;

import com.moddynerd.transiq.payment.adjustment.dto.AdjustmentResponse;
import com.moddynerd.transiq.payment.adjustment.entity.Adjustment;
import org.springframework.stereotype.Component;

@Component
public class AdjustmentMapper {

    public AdjustmentResponse toResponse(Adjustment adjustment) {
        return new AdjustmentResponse(
                adjustment.getAdjustmentReference(),
                adjustment.getAmount(),
                adjustment.getType(),
                adjustment.getReason(),
                adjustment.getCreatedBy(),
                adjustment.getCreatedAt()
        );
    }
}
