package com.moddynerd.transiq.merchant.service;

import com.moddynerd.transiq.merchant.dto.MerchantRegistrationRequest;
import com.moddynerd.transiq.merchant.dto.MerchantResponse;

public interface MerchantService {
    MerchantResponse registerMerchant(MerchantRegistrationRequest request);
}
