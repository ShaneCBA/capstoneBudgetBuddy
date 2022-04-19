package com.github.shanecba.capstone.repository;

import com.github.shanecba.capstone.entity.Pet;
import org.springframework.data.repository.CrudRepository;

public interface PetRepository extends CrudRepository<Pet, Integer> {
}
