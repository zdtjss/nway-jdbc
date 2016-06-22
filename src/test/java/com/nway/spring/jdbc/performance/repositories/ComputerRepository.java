package com.nway.spring.jdbc.performance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nway.spring.jdbc.performance.entity.Computer;

public interface ComputerRepository extends JpaRepository<Computer, Integer>
{
    
}
