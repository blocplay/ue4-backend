package com.tokenplay.ue4.filters;

import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Component(value = "Log4UUIDFilter")
public class Log4jMDCFilter extends OncePerRequestFilter {

    private static final String MDC_UUID_TOKEN_KEY = "Log4UUIDFilter.UUID";

    public Log4jMDCFilter() {
        // Default constructor as per spec.
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
        throws java.io.IOException, ServletException {
        try {
            final String uuid = UUID.randomUUID().toString().toUpperCase().replace("-", "");
            MDC.put(MDC_UUID_TOKEN_KEY, uuid);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_UUID_TOKEN_KEY);
        }
    }

    @Override
    protected boolean isAsyncDispatch(final HttpServletRequest request) {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}
