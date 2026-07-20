package com.moddynerd.transiq.payment.gateway.metadata;

import com.moddynerd.transiq.payment.gateway.bin.BinRecord;
import org.springframework.stereotype.Component;

@Component
public class CardMetadataFactory {

    public CardMetadata from(BinRecord record) {
        return new CardMetadata(
                record.issuerBank(),
                record.cardNetwork(),
                record.cardBrand(),
                record.country(),
                record.cardType(),
                record.issuerName()
        );
    }

}
