package com.moddynerd.transiq.dashboard.service;

import com.moddynerd.transiq.auth.entity.MerchantUser;
import com.moddynerd.transiq.auth.security.AuthenticatedUser;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.shared.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentJwtUserServiceImpl implements CurrentJwtUserService {

    @Override
    public MerchantUser getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser)) {
            throw new UnauthorizedException("JWT authentication required");
        }

        return authenticatedUser.getUser();
    }

    @Override
    public Merchant getCurrentMerchant() {
        return getCurrentUser().getMerchant();
    }
}
