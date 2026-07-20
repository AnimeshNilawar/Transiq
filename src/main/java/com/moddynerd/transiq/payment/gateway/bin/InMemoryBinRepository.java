package com.moddynerd.transiq.payment.gateway.bin;

import com.moddynerd.transiq.payment.gateway.card.CardBrand;
import com.moddynerd.transiq.payment.gateway.card.CardType;
import com.moddynerd.transiq.payment.gateway.model.BankCode;
import com.moddynerd.transiq.payment.gateway.model.CardNetwork;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class InMemoryBinRepository implements BinRepository {

    private final Map<String, BinRecord> bins = Map.ofEntries(
            Map.entry("411111", new BinRecord("411111", BankCode.HDFC, CardNetwork.VISA, CardBrand.VISA, CardType.CREDIT, "IN", "HDFC Bank")),
            Map.entry("422222", new BinRecord("422222", BankCode.HDFC, CardNetwork.VISA, CardBrand.VISA, CardType.DEBIT, "IN", "HDFC Bank")),
            Map.entry("431234", new BinRecord("431234", BankCode.ICICI, CardNetwork.VISA, CardBrand.VISA, CardType.CREDIT, "IN", "ICICI Bank")),
            Map.entry("456789", new BinRecord("456789", BankCode.AXIS, CardNetwork.VISA, CardBrand.VISA, CardType.CREDIT, "IN", "Axis Bank")),
            Map.entry("511111", new BinRecord("511111", BankCode.ICICI, CardNetwork.MASTERCARD, CardBrand.MASTERCARD, CardType.CREDIT, "IN", "ICICI Bank")),
            Map.entry("520000", new BinRecord("520000", BankCode.HDFC, CardNetwork.MASTERCARD, CardBrand.MASTERCARD, CardType.DEBIT, "IN", "HDFC Bank")),
            Map.entry("530000", new BinRecord("530000", BankCode.SBI, CardNetwork.MASTERCARD, CardBrand.MASTERCARD, CardType.CREDIT, "IN", "SBI Card")),
            Map.entry("550000", new BinRecord("550000", BankCode.ICICI, CardNetwork.MASTERCARD, CardBrand.MASTERCARD, CardType.CREDIT, "IN", "ICICI Bank")),
            Map.entry("600001", new BinRecord("600001", BankCode.SBI, CardNetwork.RUPAY, CardBrand.RUPAY, CardType.DEBIT, "IN", "SBI")),
            Map.entry("652150", new BinRecord("652150", BankCode.SBI, CardNetwork.RUPAY, CardBrand.RUPAY, CardType.CREDIT, "IN", "SBI Card")),
            Map.entry("652200", new BinRecord("652200", BankCode.HDFC, CardNetwork.RUPAY, CardBrand.RUPAY, CardType.CREDIT, "IN", "HDFC Bank")),
            Map.entry("652300", new BinRecord("652300", BankCode.ICICI, CardNetwork.RUPAY, CardBrand.RUPAY, CardType.PREPAID, "IN", "ICICI Bank"))
    );

    @Override
    public BinRecord find(String bin) {
        return bins.get(bin);
    }

    @Override
    public boolean exists(String bin) {
        return bins.containsKey(bin);
    }

    @Override
    public Collection<BinRecord> getAll() {
        return bins.values();
    }

}
