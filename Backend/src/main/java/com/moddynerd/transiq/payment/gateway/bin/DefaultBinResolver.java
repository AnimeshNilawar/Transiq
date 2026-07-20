package com.moddynerd.transiq.payment.gateway.bin;

import com.moddynerd.transiq.payment.gateway.card.Card;
import org.springframework.stereotype.Component;

@Component
public class DefaultBinResolver implements BinResolver {

    private final BinRepository binRepository;

    public DefaultBinResolver(BinRepository binRepository) {
        this.binRepository = binRepository;
    }

    @Override
    public BinRecord resolve(Card card) {
        BinRecord record = binRepository.find(card.bin());
        if (record == null) {
            throw new BinNotFoundException(card.bin());
        }
        return record;
    }

}
