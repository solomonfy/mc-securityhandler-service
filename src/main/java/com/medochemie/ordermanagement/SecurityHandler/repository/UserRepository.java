package com.medochemie.ordermanagement.SecurityHandler.repository;

import com.medochemie.ordermanagement.SecurityHandler.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUserName(String userName);
}
