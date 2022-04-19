package com.github.shanecba.capstone.repository;

import com.github.shanecba.capstone.entity.Goal;
import com.github.shanecba.capstone.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface GoalRepository extends CrudRepository<Goal, Integer> {
}
