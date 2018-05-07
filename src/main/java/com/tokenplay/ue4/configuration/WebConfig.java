package com.tokenplay.ue4.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.filters.CorsFilter;

@Configuration
@ComponentScan
@Slf4j
public class WebConfig extends WebMvcAutoConfigurationAdapter {
    @Value("${static.home:}")
    String home;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/app/").setViewName("forward:/app/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        if (StringUtils.isNotBlank(home)) {
            log.info("Configuring static home at {} ", home + "/UIResources/");
            registry.addResourceHandler("/res/**").addResourceLocations(home + "/UIResources/");
        } else {
            log.debug("Static home not configured.");
        }
    }

    @Bean(name = "corsFilter")
    public CorsFilter buildCorsFilter() {
        return new CorsFilter();
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true);
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return builder;
    }
}
