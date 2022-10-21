package com.medochemie.ordermanagement.SecurityHandler.repository;

import com.medochemie.ordermanagement.SecurityHandler.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {

    Role findByRoleName(String roleName);
}
