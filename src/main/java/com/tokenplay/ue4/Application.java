package com.tokenplay.ue4;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;

//import com.tokenplay.ue4.configuration.CustomLog4J2LoggingSystem;

@Configuration
@EnableConfigurationProperties
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.tokenplay.ue4"})
@EnableScheduling
@EnableAsync
@EnableRetry
public class Application extends SpringBootServletInitializer {

    private static Class<Application> appClass = Application.class;

    public static void main(final String[] args) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.setProperty("current.date", dateFormat.format(new Date()));
        SpringApplication.run(appClass, args);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(appClass);
    }
}
