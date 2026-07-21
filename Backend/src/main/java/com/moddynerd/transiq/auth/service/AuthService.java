package com.moddynerd.transiq.auth.service;

import com.moddynerd.transiq.auth.dto.LoginRequest;
import com.moddynerd.transiq.auth.dto.LoginResponse;
import com.moddynerd.transiq.auth.dto.RegisterRequest;
import com.moddynerd.transiq.auth.entity.MerchantUser;
import com.moddynerd.transiq.auth.exception.ForbiddenException;
import com.moddynerd.transiq.shared.exception.UnauthorizedException;
import com.moddynerd.transiq.auth.repository.MerchantUserRepository;
import com.moddynerd.transiq.merchant.entity.Merchant;
import com.moddynerd.transiq.merchant.exception.MerchantAlreadyExistsException;
import com.moddynerd.transiq.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MerchantRepository merchantRepository;
    private final MerchantUserRepository merchantUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Transactional
    public void register(RegisterRequest request) {
        if(merchantRepository.existsByBusinessEmail(request.getBusinessEmail())){
            throw new MerchantAlreadyExistsException(request.getBusinessEmail());
        }

        if(merchantUserRepository.existsByEmail(request.getEmail())){
            throw new ForbiddenException("User already exists with email: " + request.getEmail());
        }

        Merchant merchant = Merchant.builder()
                .businessName(request.getBusinessName())
                .businessEmail(request.getBusinessEmail())
                .build();

        Merchant savedMerchant = merchantRepository.save(merchant);

        MerchantUser merchantUser = MerchantUser.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .merchant(savedMerchant)
                .build();

        merchantUserRepository.save(merchantUser);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        MerchantUser user = merchantUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled");
        }

        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponse(
                accessToken,
                "Bearer"
        );
    }


}
