package com.github.shanecba.capstone.repository;

import com.github.shanecba.capstone.entity.InventoryItem;
import org.springframework.data.repository.CrudRepository;

public interface InventoryItemRepository extends CrudRepository<InventoryItem, Integer> {
}
