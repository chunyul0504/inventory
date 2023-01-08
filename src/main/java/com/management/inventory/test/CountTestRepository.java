package com.management.inventory.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountTestRepository extends JpaRepository<CountTest, Long> {
}
