package com.scube.document.config;

import com.c4_soft.springaddons.security.oidc.spring.C4MethodSecurityExpressionRoot;
import com.scube.auth.library.ITokenService;
import com.scube.auth.library.MyOpenIdClaimSet;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public abstract class MyMethodSecurityExpressionRoot extends C4MethodSecurityExpressionRoot {
    public abstract ITokenService getTokenService();

    @NonNull
    protected MyOpenIdClaimSet getLoggedInUser() {
        return getTokenService().getLoggedInUserInfo();
    }

    @Nullable
    protected String getLoggedInUserId() {
        return getLoggedInUser().getSubject();
    }

    @Nullable
    protected String getLoggedInUsername() {
        return getLoggedInUser().getPreferredUsername();
    }
}