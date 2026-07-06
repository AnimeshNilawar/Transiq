package com.moddynerd.transiq.merchant.mapper;

import com.moddynerd.transiq.merchant.dto.MerchantRegistrationRequest;
import com.moddynerd.transiq.merchant.dto.MerchantResponse;
import com.moddynerd.transiq.merchant.entity.Merchant;

public class MerchantMapper {

    public static Merchant toEntity(MerchantRegistrationRequest request){
        return Merchant.builder()
                .businessName(request.getBusinessName())
                .businessEmail(request.getBusinessEmail())
                .build();
    }

    public static MerchantResponse toResponse(Merchant merchant){
        return MerchantResponse.builder()
                .id(merchant.getId())
                .businessName(merchant.getBusinessName())
                .businessEmail(merchant.getBusinessEmail())
                .status(merchant.getStatus())
                .createdAt(merchant.getCreatedAt())
                .build();
    }
}
