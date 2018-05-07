package com.tokenplay.ue4.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.records.UsersRecord;
import com.tokenplay.ue4.model.repositories.UsersDB;

@Component("customAuthenticationProvider")
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UsersDB users;

    private static final SimpleGrantedAuthority DEFAULT_AUTHORITY = new SimpleGrantedAuthority("ROLE_ADMIN");

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        Authentication result = null;
        if (StringUtils.isNoneBlank(username, password)) {
            try {
                UsersRecord user = users.findByEmail(username);
                if (user != null && users.validates(user, password) && users.isDeveloper(user)) {
                    List<GrantedAuthority> grantedAuths = new ArrayList<>();
                    grantedAuths.add(DEFAULT_AUTHORITY);
                    result = new UsernamePasswordAuthenticationToken(username, password, grantedAuths);
                } else if (user == null) {
                    log.info("User not found: {}", username);
                } else if (!users.isDeveloper(user)) {
                    log.info("A simple user, {}, tried to access the admin panel", username);
                } else {
                    log.info("User, {}, entered an incorrect password", username);
                }
            } catch (Exception e) {
                log.error("Error authenticating user.", e);
            }
        } else {
            log.error("Error authenticating user. Username and password are mandatory");
        }
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
