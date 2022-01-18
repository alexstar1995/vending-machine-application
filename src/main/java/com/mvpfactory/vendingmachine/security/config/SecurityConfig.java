package com.mvpfactory.vendingmachine.security.config;

import com.mvpfactory.vendingmachine.model.Role;
import com.mvpfactory.vendingmachine.security.AuthUserDetailsService;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/api/v1/users/signup", "/login*").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/products*").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                .antMatchers("/api/v1/users*").hasAnyRole(Role.BUYER.toString(), Role.SELLER.toString())
                .antMatchers("/api/v1/users/**/deposit", "/api/v1/users/**/reset", "/api/v1/products/**/buy").hasRole(Role.BUYER.name())
                .antMatchers(HttpMethod.POST, "/api/v1/products").hasRole(Role.SELLER.toString())
                .antMatchers(HttpMethod.PUT, "/api/v1/products").hasRole(Role.SELLER.toString())
                .antMatchers(HttpMethod.DELETE,"/api/v1/products/**").hasRole(Role.SELLER.toString())
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
