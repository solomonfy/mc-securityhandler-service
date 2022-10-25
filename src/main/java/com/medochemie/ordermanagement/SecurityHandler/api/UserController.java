package com.medochemie.ordermanagement.SecurityHandler.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medochemie.ordermanagement.SecurityHandler.entity.Role;
import com.medochemie.ordermanagement.SecurityHandler.entity.User;
import com.medochemie.ordermanagement.SecurityHandler.service.UserService;
import com.medochemie.ordermanagement.SecurityHandler.utility.Constant;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constant.USER_CONTROLLER_END_POINT)
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id){
        Optional<User> optionalUser = userService.getUserById(id);
        User user = optionalUser.get();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/userName/{userName}")
    public ResponseEntity<User> getUserByUserName(@PathVariable String userName){
        User user = userService.getUser(userName);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{agentId}/save")
    public ResponseEntity<User> saveUser(@RequestBody User user, @PathVariable String agentId){

        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(Constant.USER_CONTROLLER_END_POINT + "/" + agentId + "/save")
                .toUriString());
        System.out.println(uri);
        return ResponseEntity.created(uri).body(userService.saveUser(user, agentId));
    }

    @PostMapping("/save/role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(Constant.USER_CONTROLLER_END_POINT+ "/save/role")
                .toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/role/addRoleToUser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form){
        String userName = form.getUserName();
        String roleName = form.getRoleName();
        userService.addRoleToUser(userName, roleName);
        return ResponseEntity.ok().build();
    }

    //This method sends a new access token if refresh token is provided.
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Get the authorizationHeader (if header is "Authorization")
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        //If authorizationHeader is not null & starts with "Bearer ",
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                //Get refresh_token from authorizationHeader by removing the Bearer word
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                //Use same algorithm and secret
                Algorithm algorithm  =Algorithm.HMAC256("secret".getBytes());
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                //Decode the token with JWT verifier
                DecodedJWT decodedJWT = jwtVerifier.verify(refresh_token);
                //Get username from the decoded token, we don't need the password since we do have the token
                String userName = decodedJWT.getSubject();
                User user = userService.getUser(userName);
                String access_token = JWT.create()
                        .withSubject(user.getUserName())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                        .withIssuer(request.getRequestURI().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);

                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

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
            throw new RuntimeException("Refresh token is missing!");
        }
    }


}
    @Data
    class RoleToUserForm{
        private String userName;
        private String roleName;
    }
