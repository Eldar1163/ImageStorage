package com.example.imagestorage.security;

import com.example.imagestorage.properties.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class BasicWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {


    private final MyBasicAuthenticationEntryPoint authenticationEntryPoint;
    private final SecurityConfig config;
    private final PasswordEncoder passwordEncoder;

    public BasicWebSecurityConfigurerAdapter(MyBasicAuthenticationEntryPoint authenticationEntryPoint, SecurityConfig config, PasswordEncoder passwordEncoder) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.config = config;
        this.passwordEncoder = passwordEncoder;
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        String username = config.getUsername();
        String password = config.getPassword();

        String encrytedPassword = passwordEncoder.encode(password);


        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> //
                mngConfig = auth.inMemoryAuthentication();

        UserDetails u1 = User.withUsername(username).password(encrytedPassword).roles("USER").build();

        mngConfig.withUser(u1);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests().anyRequest().authenticated();

        http.httpBasic().authenticationEntryPoint(authenticationEntryPoint);
    }
}
