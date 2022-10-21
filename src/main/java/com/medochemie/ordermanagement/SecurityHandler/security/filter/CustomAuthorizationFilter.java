package com.medochemie.ordermanagement.SecurityHandler.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.medochemie.ordermanagement.SecurityHandler.utilities.Constant.LOG_IN_URL;
import static com.medochemie.ordermanagement.SecurityHandler.utilities.Constant.USER_CONTROLLER_END_POINT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//This class checks if a user do have access to the app (when access token is provided)
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    final Logger logger = LoggerFactory.getLogger(CustomAuthorizationFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //If path is login or refresh token, do not try to authorize, just allow it.
        if(request.getServletPath().equals(LOG_IN_URL) ||
                request.getServletPath().equals(USER_CONTROLLER_END_POINT+"/token/refresh")){
            filterChain.doFilter(request, response);
        }
        else{
            //Get the authorizationHeader (if header is "Authorization")
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            //If authorizationHeader is not null & starts with "Bearer ",
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    //Get token from authorizationHeader by removing the Bearer word
                    String token = authorizationHeader.substring("Bearer ".length());
                    //Use same algorithm and secret
                    Algorithm algorithm  =Algorithm.HMAC256("secret".getBytes());
                    JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                    //Decode the token with JWT verifier
                    DecodedJWT decodedJWT = jwtVerifier.verify(token);
                    //Get both username & role from the decoded token, we don't need the password since we do have the token
                    String userName = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    //Convert String array of roles to SimpleGrantedAuthority
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    for (String role : roles) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userName, null, authorities);
                    //Set this user in SecurityContextHolder, telling here is the userName, the roles what the user can do.
                    //Determine what resources this user can access
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    //And let the request continue doing the filter
                    filterChain.doFilter(request,response);
                } catch (Exception exception){
                    logger.error("Error logging in: {}", exception.getMessage());
                    response.setHeader("Error logging in: {}", exception.getMessage());
                    response.setStatus(FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    error.put("access_token", exception.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            }
            else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
