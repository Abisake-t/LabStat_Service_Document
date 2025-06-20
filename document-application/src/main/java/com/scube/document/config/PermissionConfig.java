package com.scube.document.config;

import com.c4_soft.springaddons.security.oidc.spring.C4MethodSecurityExpressionHandler;
import com.scube.auth.library.ITokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;

@Configuration
public class PermissionConfig {
    @Bean
    public static MethodSecurityExpressionHandler methodSecurityExpressionHandler(ITokenService tokenService) {
        return new C4MethodSecurityExpressionHandler(() -> new MySecurityExpressionRoot(tokenService));
    }
}