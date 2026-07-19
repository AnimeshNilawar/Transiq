package com.moddynerd.transiq.shared.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "transiq.security")
public class SecurityProperties {

    private String encryptionKey;

}