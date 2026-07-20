package com.moddynerd.transiq.dashboard.service;

import com.moddynerd.transiq.auth.entity.MerchantUser;
import com.moddynerd.transiq.merchant.entity.Merchant;

public interface CurrentJwtUserService {

    MerchantUser getCurrentUser();

    Merchant getCurrentMerchant();

}
