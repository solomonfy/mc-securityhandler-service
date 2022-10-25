package com.medochemie.ordermanagement.SecurityHandler.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFilter.class);
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //Get the username & password from the request
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        logger.info("UserVO name is: {} and password: {}", userName, password);
        //Created a new UsernamePasswordAuthenticationToken obj from the req body,
        // and pass it to authenticationManager.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, password);
//        System.out.println(authenticationToken.isAuthenticated());
        return authenticationManager.authenticate(authenticationToken);
    }

    //When this method is called, will send access and refresh tokens in response body once authentication is ok.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        //Get the authenticated user from UserDetailsService of spring, and then create the token.
        User user = (User) authentication.getPrincipal();

        //Generate token, sign it but doing it manually is a lot of work.
        //So use a library, java-jwt add it to pom file
        Algorithm algorithm  =Algorithm.HMAC256("secret".getBytes());

        //short span, so 10min
        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                .withIssuer(request.getRequestURI().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        //longer span, like a week and no need of the roles
        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 7*24*60*60*1000))
                .withIssuer(request.getRequestURI().toString())
                .sign(algorithm);

//        response.setHeader("access_token", access_token);
//        response.setHeader("refresh_token", refresh_token);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);

    }

    //If wanted to check how many times unsuccessful attempts are done in specific window,
    // and if exceeded, lock account.
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
//        super.unsuccessfulAuthentication(request, response, failed);
//    }
}
