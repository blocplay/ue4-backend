package com.tokenplay.ue4.configuration;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tokenplay.ue4.filters.Log4jMDCFilter;

@Configuration
public class Log4jMDCFilterConfiguration {

    @Bean
    public FilterRegistrationBean servletRegistrationBean() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        final Log4jMDCFilter log4jMDCFilterFilter = new Log4jMDCFilter();
        registrationBean.setFilter(log4jMDCFilterFilter);
        registrationBean.setOrder(2);
        return registrationBean;
    }
}
