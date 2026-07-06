package com.moddynerd.transiq.merchant.service;

import com.moddynerd.transiq.merchant.dto.MerchantRegistrationRequest;
import com.moddynerd.transiq.merchant.dto.MerchantResponse;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.merchant.exception.MerchantAlreadyExistsException;
import com.moddynerd.transiq.merchant.mapper.MerchantMapper;
import com.moddynerd.transiq.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService{

    private final MerchantRepository merchantRepository;

    @Override
    public MerchantResponse registerMerchant(MerchantRegistrationRequest request) {
        if (merchantRepository.existsByBusinessEmail(request.getBusinessEmail())) {
            throw new MerchantAlreadyExistsException(request.getBusinessEmail());
        }

        Merchant merchant = MerchantMapper.toEntity(request);

        Merchant savedMerchant = merchantRepository.save(merchant);

        return MerchantMapper.toResponse(savedMerchant);
    }
}
