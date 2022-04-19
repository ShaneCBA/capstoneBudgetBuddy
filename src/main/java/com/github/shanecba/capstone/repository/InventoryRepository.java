package com.github.shanecba.capstone.repository;

import com.github.shanecba.capstone.entity.Inventory;
import com.github.shanecba.capstone.entity.InventoryItem;
import org.springframework.data.repository.CrudRepository;

public interface InventoryRepository extends CrudRepository<Inventory, Integer> {
}
