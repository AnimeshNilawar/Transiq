package com.moddynerd.transiq.merchant.service;

import com.moddynerd.transiq.merchant.dto.MerchantRegistrationRequest;
import com.moddynerd.transiq.merchant.dto.MerchantResponse;

import java.util.UUID;

public interface MerchantService {
    MerchantResponse registerMerchant(MerchantRegistrationRequest request);

    MerchantResponse suspendMerchant(UUID merchantId);

    MerchantResponse activateMerchant(UUID merchantId);
}
