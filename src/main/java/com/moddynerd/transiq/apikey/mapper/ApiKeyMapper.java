package com.moddynerd.transiq.apikey.mapper;

import com.moddynerd.transiq.apikey.dto.ApiKeyResponse;
import com.moddynerd.transiq.apikey.dto.CreateApiKeyResponse;
import com.moddynerd.transiq.apikey.entity.ApiKey;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyMapper {

    public CreateApiKeyResponse toCreateResponse(
            ApiKey apikey,
            String plainApiKey
    ) {
        return new CreateApiKeyResponse(
                apikey.getId(),
                plainApiKey,
                apikey.getKeyPrefix(),
                apikey.getCreatedAt()
        );
    }

    public ApiKeyResponse toResponse(ApiKey apikey){
        return new ApiKeyResponse(
                apikey.getId(),
                apikey.getName(),
                apikey.getKeyPrefix(),
                apikey.getEnvironment(),
                apikey.getType(),
                apikey.getStatus(),
                apikey.getLastUsedAt(),
                apikey.getCreatedAt()
        );
    }
}
