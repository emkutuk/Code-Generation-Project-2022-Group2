package io.swagger.security;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority
{
    ROLE_EMPLOYEE,
    ROLE_CUSTOMER;

    @Override
    public String getAuthority() {
        return name();
    }
}
