package com.kh.ct.domain.emp.repository;

import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.health.dto.HealthDto;
import com.kh.ct.global.common.CommonEnums;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmpRepository extends JpaRepository<Emp, String> {

    List<Emp> findByEmpStatus(CommonEnums.EmpStatus status);

    List<Emp> findByEmpNameContainingAndEmpStatus(String keyword, CommonEnums.EmpStatus status);

    Optional<Emp> findByEmpIdAndEmpStatus(String empId, CommonEnums.EmpStatus status);
    
    Optional<Emp> findByAirlineIdAndRole(com.kh.ct.domain.emp.entity.Airline airline, CommonEnums.Role role);
    
    Optional<Emp> findByEmailAndRole(String email, CommonEnums.Role role);

    List<Emp> findByAirlineId_AirlineIdAndJob(Long airlineId, String job);

    Optional<Emp> findByEmpNameAndEmailAndEmpStatus(String empName, String email, CommonEnums.EmpStatus status);

    Optional<Emp> findByEmpIdAndEmailAndEmpStatus(String empId, String email, CommonEnums.EmpStatus status);

    // 직원 상세 정보 조회 (JOIN FETCH로 LAZY 직렬화 문제 방지)
    @Query("SELECT e FROM Emp e " +
           "LEFT JOIN FETCH e.departmentId dept " +
           "LEFT JOIN FETCH e.airlineId airline " +
           "WHERE e.empId = :empId")
    Optional<Emp> findByIdWithDetails(@Param("empId") String empId);

    boolean existsByEmpNo(String empNo);

    @Query("""
        select max(e.empNo)
        from Emp e
        where e.empNo like concat(:yearPrefix, '%')
    """)
    String findMaxEmpNoByYearPrefix(@Param("yearPrefix") String yearPrefix);

    @Query(
            value = """
    select new com.kh.ct.domain.health.dto.HealthDto$AdminEmpHealthRow(
      e.empId,
      e.empName,
      d.departmentName,
      e.job,
      e.startDate,
      (select max(pt.testDate)from EmpPhysicalTest pt where pt.empId.empId = e.empId),
      (select eh.healthPoint
         from EmpHealth eh
        where eh.empId = e
          and eh.createDate = (
              select max(eh2.createDate)
              from EmpHealth eh2
              where eh2.empId = e
          )
      )
    )
    from Emp e
    left join e.departmentId d
    where (:empName is null or :empName = '' or e.empName like concat('%', :empName, '%'))
    order by e.empName asc
  """,
            countQuery = """
    select count(e)
    from Emp e
    where (:empName is null or :empName = '' or e.empName like concat('%', :empName, '%'))
  """
    )
    Page<HealthDto.AdminEmpHealthRow> findAdminEmpHealthRows(@Param("empName") String empName, Pageable pageable);

    // 역할별 직원 조회 (JOIN FETCH로 LAZY 직렬화 문제 방지)
    @Query("""
        SELECT DISTINCT e
        FROM Emp e
        LEFT JOIN FETCH e.departmentId dept
        LEFT JOIN FETCH e.airlineId airline
        WHERE (:role IS NULL OR e.role = :role)
          AND (:airlineId IS NULL OR airline.airlineId = :airlineId)
        ORDER BY e.empName ASC
    """)
    List<Emp> findByRoleAndAirlineId(
            @Param("role") CommonEnums.Role role,
            @Param("airlineId") Long airlineId
    );


    List<Emp> findByAirlineId_AirlineIdAndDepartmentId_DepartmentIdAndEmpStatusAndEmpIdNot(
            Long airlineId,
            Long departmentId,
            CommonEnums.EmpStatus empStatus,
            String empIdNot
    );
}

