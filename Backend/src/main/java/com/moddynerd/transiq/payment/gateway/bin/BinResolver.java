package com.moddynerd.transiq.payment.gateway.bin;

import com.moddynerd.transiq.payment.gateway.card.Card;

public interface BinResolver {

    BinRecord resolve(Card card);

}
