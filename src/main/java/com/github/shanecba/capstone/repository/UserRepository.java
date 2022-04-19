package com.github.shanecba.capstone.repository;

import com.github.shanecba.capstone.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    public User findBySub(String sub);
}
