package com.nway.spring.jdbc.performance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nway.spring.jdbc.performance.Performance;
import com.nway.spring.jdbc.performance.entity.Monitor;

public interface MonitorRepository extends JpaRepository<Monitor, Integer>, Performance
{
    
}
