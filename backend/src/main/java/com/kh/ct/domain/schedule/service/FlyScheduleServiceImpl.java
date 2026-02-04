package com.kh.ct.domain.schedule.service;

import com.kh.ct.domain.emp.entity.Airline;
import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.emp.repository.AirlineRepository;
import com.kh.ct.domain.emp.repository.EmpRepository;
import com.kh.ct.domain.schedule.dto.FlyScheduleDto;
import com.kh.ct.domain.schedule.entity.Airport;
import com.kh.ct.domain.schedule.entity.EmpFlySchedule;
import com.kh.ct.domain.schedule.entity.FlySchedule;
import com.kh.ct.domain.schedule.repository.AirportRepository;
import com.kh.ct.domain.schedule.repository.EmpFlyScheduleRepository;
import com.kh.ct.domain.schedule.repository.EmpScheduleRepository;
import com.kh.ct.domain.schedule.repository.FlyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlyScheduleServiceImpl implements FlyScheduleService {
    
    private final FlyScheduleRepository flyScheduleRepository;
    private final EmpScheduleRepository empScheduleRepository;
    private final EmpFlyScheduleRepository empFlyScheduleRepository;
    private final EmpRepository empRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    
    @Override
    public List<FlyScheduleDto.ListResponse> getFlightSchedules(
            Long airlineId,
            String empId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String departure,
            String destination
    ) {
        List<FlySchedule> schedules;
        
        try {
            // 직원인 경우 본인이 배정된 비행편만 조회
            if (empId != null) {
                schedules = flyScheduleRepository.findByEmpId(empId);
            } else {
                // 관리자인 경우 필터 조건에 따라 조회
                if (startDate != null && endDate != null) {
                    schedules = flyScheduleRepository.findByDateRange(airlineId, startDate, endDate);
                } else if (departure != null || destination != null) {
                    schedules = flyScheduleRepository.findByDepartureAndDestination(airlineId, departure, destination);
                } else {
                    // airlineId가 null이면 전체 조회
                    if (airlineId == null) {
                        schedules = flyScheduleRepository.findAllByOrderByFlyStartTimeAsc();
                    } else {
                        schedules = flyScheduleRepository.findByAirlineId(airlineId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("비행편 조회 중 오류 발생: " + e.getMessage(), e);
        }
        
        // 직원이 배정된 비행편 ID 목록 (isAssignedToMe 판단용) - EmpFlySchedule 사용
        Set<Long> assignedScheduleIds = null;
        if (empId != null) {
            assignedScheduleIds = empFlyScheduleRepository.findByEmpId(empId).stream()
                    .map(efs -> efs.getFlySchedule().getFlyScheduleId())
                    .collect(Collectors.toSet());
        }
        
        // 모든 비행편 ID 목록
        List<Long> flyScheduleIds = schedules.stream()
                .map(FlySchedule::getFlyScheduleId)
                .collect(Collectors.toList());
        
        // 각 비행편에 배정된 직원 수 조회 (크루 배정 여부 판단용)
        Map<Long, Long> crewCountMap = new java.util.HashMap<>();
        if (!flyScheduleIds.isEmpty()) {
            // 각 비행편별로 배정된 직원 수 조회
            for (Long scheduleId : flyScheduleIds) {
                long count = empFlyScheduleRepository.findByFlyScheduleId(scheduleId).size();
                crewCountMap.put(scheduleId, count);
            }
        }
        
        final Set<Long> finalAssignedScheduleIds = assignedScheduleIds;
        final Map<Long, Long> finalCrewCountMap = crewCountMap;
        
        return schedules.stream()
                .map(fs -> convertToListResponse(fs, finalAssignedScheduleIds, finalCrewCountMap))
                .collect(Collectors.toList());
    }
    
    @Override
    public FlyScheduleDto getFlightScheduleDetail(Long flyScheduleId, String empId) {
        FlySchedule flySchedule = flyScheduleRepository.findByFlyScheduleId(flyScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("비행편을 찾을 수 없습니다. (flyScheduleId: " + flyScheduleId + ")"));
        
        // 크루 멤버 조회 (EmpFlySchedule 사용)
        List<EmpFlySchedule> empFlySchedules = empFlyScheduleRepository.findByFlyScheduleId(flyScheduleId);
        System.out.println("비행편 ID " + flyScheduleId + "에 배정된 직원 수: " + empFlySchedules.size());
        
        List<FlyScheduleDto.CrewMemberResponse> crewMembers = empFlySchedules.stream()
                .map(efs -> {
                    Emp emp = efs.getEmp();
                    if (emp == null) {
                        System.err.println("EmpFlySchedule ID " + efs.getEmpFlyScheduleId() + "의 Emp가 null입니다.");
                        return null;
                    }
                    
                    // Department 정보 가져오기
                    String departmentName = null;
                    if (emp.getDepartmentId() != null) {
                        departmentName = emp.getDepartmentId().getDepartmentName();
                    }
                    
                    // EmpStatus 정보 가져오기
                    String empStatus = null;
                    if (emp.getEmpStatus() != null) {
                        empStatus = emp.getEmpStatus().name();
                    }
                    
                    return FlyScheduleDto.CrewMemberResponse.builder()
                            .empId(emp.getEmpId())
                            .empName(emp.getEmpName())
                            .role(emp.getRole() != null ? emp.getRole().name() : null)
                            .job(emp.getJob())
                            .departmentName(departmentName)
                            .empStatus(empStatus)
                            .empFlyScheduleId(efs.getEmpFlyScheduleId())
                            .build();
                })
                .filter(member -> member != null)
                .collect(Collectors.toList());
        
        System.out.println("변환된 크루 멤버 수: " + crewMembers.size());
        
        // 항공사 정보 (FlySchedule의 airlineId 사용)
        Long airlineId = flySchedule.getAirlineId();
        String airlineName = null;
        if (airlineId != null) {
            Airline airline = airlineRepository.findById(airlineId).orElse(null);
            if (airline != null) {
                airlineName = airline.getAirlineName();
            }
        }
        
        // 시간 포맷팅
        String departureTime = formatTime(flySchedule.getFlyStartTime());
        String arrivalTime = formatTime(flySchedule.getFlyEndTime());
        String duration = calculateDuration(
            flySchedule.getFlyStartTime(),
            flySchedule.getFlyEndTime(),
            flySchedule.getDeparture(),
            flySchedule.getDestination()
        );
        
        return FlyScheduleDto.builder()
                .flyScheduleId(flySchedule.getFlyScheduleId())
                .flightNumber(flySchedule.getFlightNumber())
                .airplaneType(flySchedule.getAirplaneType())
                .departure(flySchedule.getDeparture())
                .flyStartTime(flySchedule.getFlyStartTime())
                .destination(flySchedule.getDestination())
                .flyEndTime(flySchedule.getFlyEndTime())
                .gate(flySchedule.getGate())
                .crewCount(flySchedule.getCrewCount() != null ? flySchedule.getCrewCount().intValue() : null)
                .flightStatus(flySchedule.getFlightStatus())
                .seatCount(flySchedule.getSeatCount() != null ? flySchedule.getSeatCount().intValue() : null)
                .crewMembers(crewMembers)
                .airlineId(airlineId)
                .airlineName(airlineName)
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .duration(duration)
                .build();
    }
    
    private FlyScheduleDto.ListResponse convertToListResponse(FlySchedule fs, Set<Long> assignedScheduleIds, Map<Long, Long> crewCountMap) {
        // 시간 포맷팅
        String departureTime = formatTime(fs.getFlyStartTime());
        String arrivalTime = formatTime(fs.getFlyEndTime());
        String duration = calculateDuration(
            fs.getFlyStartTime(), 
            fs.getFlyEndTime(),
            fs.getDeparture(),
            fs.getDestination()
        );
        
        // 크루 배정 여부 (실제 EmpFlySchedule에서 조회한 배정 직원 수로 판단)
        Long crewCount = crewCountMap.getOrDefault(fs.getFlyScheduleId(), 0L);
        boolean crewAssigned = crewCount > 0;
        
        // 본인 배정 여부 (flyScheduleId로 판단)
        boolean isAssignedToMe = assignedScheduleIds != null 
                && assignedScheduleIds.contains(fs.getFlyScheduleId());
        
        // 항공사 정보 (FlySchedule의 airlineId 직접 사용)
        Long airlineId = fs.getAirlineId();
        String airlineName = null;
        if (airlineId != null) {
            Airline airline = airlineRepository.findById(airlineId).orElse(null);
            if (airline != null) {
                airlineName = airline.getAirlineName();
            }
        }
        
        return FlyScheduleDto.ListResponse.builder()
                .flyScheduleId(fs.getFlyScheduleId())
                .flightNumber(fs.getFlightNumber())
                .departure(fs.getDeparture())
                .destination(fs.getDestination())
                .flyStartTime(fs.getFlyStartTime())
                .flyEndTime(fs.getFlyEndTime())
                .flightStatus(fs.getFlightStatus())
                .crewCount(fs.getCrewCount() != null ? fs.getCrewCount().intValue() : null)
                .crewAssigned(crewAssigned)
                .isAssignedToMe(isAssignedToMe)
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .duration(duration)
                .airlineId(airlineId)
                .airlineName(airlineName)
                .build();
    }
    
    private String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    /**
     * 비행 시간 계산 (타임존 고려)
     * 출발지와 도착지의 타임존을 고려하여 정확한 비행 시간을 계산합니다.
     */
    private String calculateDuration(LocalDateTime start, LocalDateTime end, String departureCode, String destinationCode) {
        if (start == null || end == null) return "";
        
        try {
            // 출발지 타임존 조회
            ZoneId departureZone = getZoneId(departureCode);
            // 도착지 타임존 조회
            ZoneId destinationZone = getZoneId(destinationCode);
            
            // LocalDateTime을 각 공항의 타임존으로 ZonedDateTime으로 변환
            ZonedDateTime departureZoned = start.atZone(departureZone);
            ZonedDateTime arrivalZoned = end.atZone(destinationZone);
            
            // UTC로 변환하여 Duration 계산
            Instant departureInstant = departureZoned.toInstant();
            Instant arrivalInstant = arrivalZoned.toInstant();
            
            Duration duration = Duration.between(departureInstant, arrivalInstant);
            long hours = duration.toHours();
            long minutes = Math.abs(duration.toMinutes() % 60);
            
            if (hours > 0 && minutes > 0) {
                return hours + "시간 " + minutes + "분";
            } else if (hours > 0) {
                return hours + "시간";
            } else {
                return minutes + "분";
            }
        } catch (Exception e) {
            // 타임존 조회 실패 시 기존 방식으로 계산 (fallback)
            Duration duration = Duration.between(start, end);
            long hours = duration.toHours();
            long minutes = Math.abs(duration.toMinutes() % 60);
            
            if (hours > 0 && minutes > 0) {
                return hours + "시간 " + minutes + "분";
            } else if (hours > 0) {
                return hours + "시간";
            } else {
                return minutes + "분";
            }
        }
    }
    
    /**
     * 공항 코드로 ZoneId 조회
     */
    private ZoneId getZoneId(String airportCode) {
        if (airportCode == null || airportCode.trim().isEmpty()) {
            return ZoneId.systemDefault(); // 기본값: 시스템 타임존
        }
        
        try {
            // 공항 코드로 Airport 조회
            Airport airport = airportRepository.findByAirportCode(airportCode.trim().toUpperCase())
                    .orElse(null);
            
            if (airport != null && airport.getTimezone() != null && !airport.getTimezone().trim().isEmpty()) {
                // Airport 엔티티의 timezone 필드 사용 (예: "Asia/Seoul", "America/Los_Angeles")
                return ZoneId.of(airport.getTimezone());
            }
            
            // 공항 정보가 없거나 타임존이 없으면 기본 타임존 사용
            return ZoneId.systemDefault();
        } catch (Exception e) {
            // ZoneId 파싱 실패 시 기본 타임존 사용
            return ZoneId.systemDefault();
        }
    }
}
