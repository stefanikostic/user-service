package com.evelateresume.user_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("token.cookie")
@Getter
@Setter
public class TokenCookieConfiguration {

    private String name;
    private int maxAge;
    private boolean httpOnly;
    private boolean secure;
    private String path;

}
