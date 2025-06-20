package com.scube.document.config;

import com.scube.auth.library.ITokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MySecurityExpressionRoot extends MyMethodSecurityExpressionRoot {
    private final ITokenService tokenService;

    @Override
    public ITokenService getTokenService() {
        return tokenService;
    }

    public boolean hasPermission(String permission) {
        log.info("Checking if user: {} has permission: {}", getLoggedInUsername(), permission);
        return hasAuthority(permission);
    }
}