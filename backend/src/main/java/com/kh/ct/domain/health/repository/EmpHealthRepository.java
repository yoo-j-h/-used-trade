package com.kh.ct.domain.health.repository;

import com.kh.ct.domain.health.entity.EmpHealth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpHealthRepository extends JpaRepository<EmpHealth, String> {

    Optional<EmpHealth> findTopByEmpId_EmpIdOrderByEmpHealthIdDesc(String empId);

}
