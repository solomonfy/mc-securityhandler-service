package com.medochemie.ordermanagement.SecurityHandler.security;

import com.medochemie.ordermanagement.SecurityHandler.security.filter.CustomAuthenticationFilter;
import com.medochemie.ordermanagement.SecurityHandler.security.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.medochemie.ordermanagement.SecurityHandler.utility.Constant.LOG_IN_URL;
import static com.medochemie.ordermanagement.SecurityHandler.utility.Constant.USER_CONTROLLER_END_POINT;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }


    //this is to override spring security not to handle some endpoints.
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //To override the default login url /login
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl(LOG_IN_URL);

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //Allow all requests to log in url
        http.authorizeRequests().antMatchers(LOG_IN_URL + "/**", USER_CONTROLLER_END_POINT+"/token/refresh/**").permitAll();
        http.authorizeRequests().antMatchers(USER_CONTROLLER_END_POINT + "/**").hasAnyAuthority("ROLE_USER");
        http.authorizeRequests().antMatchers(USER_CONTROLLER_END_POINT + "/save/**").hasAnyAuthority("ROLE_MANAGER");
        http.authorizeRequests().anyRequest().authenticated();

        //custom Authentication filter
        http.addFilter(customAuthenticationFilter);
        //Adding the AuthorizationFilter filter before, since we need to intercept any request
        // for the UsernamePasswordAuthenticationFilter class
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    // AuthenticationManager is present in WebSecurityConfigurerAdapter class, and we can bring it in.
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        //returning authenticationManagerBean method from the super class or WebSecurityConfigurerAdapter class
        return super.authenticationManagerBean();
    }
}
