package com.tokenplay.ue4.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.tokenplay.ue4.www.security.RestAuthenticationEntryPoint;

@Configuration
//@ImportResource("classpath:root-context.xml")
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    //    @Autowired
    //    RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
    //
    //    @Autowired
    //    RestAuthenticationFailureHandler restAuthenticationFailureHandler;
    //
    //    @Autowired
    //    RestLogoutSuccessHandler restLogoutSuccessHandler;
    //
    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        //@formatter:off
        http
            .csrf()
                .disable()
                    .authorizeRequests()
//                        .antMatchers("/**").permitAll()
                        .antMatchers("/app/**", "/srest/**")
                            .hasRole("ADMIN").and().httpBasic().realmName("ue4")
                            .authenticationEntryPoint(restAuthenticationEntryPoint)
                            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                                .and().formLogin().successHandler(restAuthenticationSuccessHandler)
//                                .failureHandler(restAuthenticationFailureHandler)
//                                .and().logout().logoutSuccessHandler(restLogoutSuccessHandler)
//                                .and().exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
                        ;
        //@formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
    }

    @Bean(name = "myAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /* To allow Pre-flight [OPTIONS] request from browser */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }
}
