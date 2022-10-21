package com.medochemie.ordermanagement.SecurityHandler.service;

import com.medochemie.ordermanagement.SecurityHandler.entity.Role;
import com.medochemie.ordermanagement.SecurityHandler.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String userName, String roleName);
    User getUser(String userName);
    Optional<User> getUserById(String id);

    // on real app, use pagination
    List<User> getUsers();
}
