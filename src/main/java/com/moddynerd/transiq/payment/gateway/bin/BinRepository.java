package com.moddynerd.transiq.payment.gateway.bin;

import java.util.Collection;

public interface BinRepository {

    BinRecord find(String bin);

    boolean exists(String bin);

    Collection<BinRecord> getAll();

}
