package com.medochemie.ordermanagement.SecurityHandler.service.impl;

import com.medochemie.ordermanagement.SecurityHandler.entity.Role;
import com.medochemie.ordermanagement.SecurityHandler.entity.User;
import com.medochemie.ordermanagement.SecurityHandler.repository.RoleRepository;
import com.medochemie.ordermanagement.SecurityHandler.repository.UserRepository;
import com.medochemie.ordermanagement.SecurityHandler.service.GenerateEmail;
import com.medochemie.ordermanagement.SecurityHandler.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service

//to inject the fields (for dependency injection)
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName);
        if(user == null) {
            logger.info("User not found in the database: {} ", userName);
            throw new UsernameNotFoundException("User not found in the database");
        }
        else{
            logger.info("User found in the database: {} ", userName);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
    }

    @Override
    public User saveUser(User user) {
        //Get agentId from the loggedIn user or admin

        if(user == null) logger.info("User is null and can't be saved");
        try{
        logger.info("Saving new user to database {} ", user.getUserName());
            //Need to encode the password before saving to db
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            //Default values for new user
            //Generate email
            user.setEmailId(GenerateEmail.generateEmail(user.getFirstName(), user.getLastName()));
//            Optional<Role> roleOptional = roleRepository.findById("635188fa4806844f891f533a");
//            Role role = roleOptional.get();
//                System.out.println(role.getRoleName());
//            user.getRoles().add(role);
            user.setActive(true);
            user.setNotLocked(true);
            user.setPhone("");
            user.setProfileImageUrl("");
            user.setCreatedBy("Solomon");
            user.setCreatedOn(new Date());
            user.setJoinDate(new Date());
            user.setUpdatedBy(null);
            user.setUpdatedOn(null);
            user.setLastLoginDate(null);
            user.setLastLoginDateDisplay(null);

            userRepository.save(user);
        } catch (Exception exception){
            logger.info(exception.getMessage());
        }
        return user;
    }

    @Override
    public Role saveRole(Role role) {
        logger.info("Saving new role to database {} ", role.getRoleName());
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String userName, String roleName) {
        try{
            User user = userRepository.findByUserName(userName);
            Role role = roleRepository.findByRoleName(roleName);
            String roleId = role.getId();
            user.getRoles().add(role);
            logger.info("Added role {} to user {} ", role.getRoleName(), user.getUserName());
            System.out.println(user.getRoles().stream());
        } catch (Exception exception) {
            logger.info(exception.getMessage());
            throw exception;
        }
    }

    @Override
    public User getUser(String userName) {
        logger.info("Retrieving user {} from database", userName);
        return userRepository.findByUserName(userName);
    }

    @Override
    public Optional<User> getUserById(String id) {
        logger.info("Retrieving user with id {} from database", id);
        return userRepository.findById(id);
    }

    @Override
    public List<User> getUsers() {
        List<User> users = null;
        try {
            users = userRepository.findAll();
            logger.info("Retrieved {} users from database! ", users.size());
        } catch (Exception exception){
            logger.info("Error while fetching users: {} ", exception.getMessage());
        }
        return users;
    }


}
