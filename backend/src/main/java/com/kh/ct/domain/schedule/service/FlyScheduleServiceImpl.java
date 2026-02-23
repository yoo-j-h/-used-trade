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
import com.kh.ct.global.common.CommonEnums;
import com.kh.ct.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
        // 통합 조회 메서드 사용 (모든 필터 조건을 한 번에 처리)
        List<FlySchedule> schedules = flyScheduleRepository.findWithFilters(
                empId,
                airlineId,
                startDate,
                endDate,
                departure != null ? departure.trim() : null,
                destination != null ? destination.trim() : null
        );
        
        // 조회된 비행편이 없으면 빈 리스트 반환
        if (schedules.isEmpty()) {
            return List.of();
        }
        
        // 모든 비행편 ID 목록
        List<Long> flyScheduleIds = schedules.stream()
                .map(FlySchedule::getFlyScheduleId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        // 직원이 배정된 비행편 ID 목록 (isAssignedToMe 판단용)
        Set<Long> assignedScheduleIds = null;
        if (empId != null && !empId.trim().isEmpty()) {
            assignedScheduleIds = empFlyScheduleRepository.findByEmpId(empId).stream()
                    .map(efs -> {
                        if (efs.getFlySchedule() != null) {
                            return efs.getFlySchedule().getFlyScheduleId();
                        }
                        return null;
                    })
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
        }
        
        // 각 비행편에 배정된 직원 수 조회 (크루 배정 여부 판단용) - 배치 조회로 최적화
        Map<Long, Long> crewCountMap = new java.util.HashMap<>();
        if (!flyScheduleIds.isEmpty()) {
            List<EmpFlySchedule> allEmpFlySchedules = empFlyScheduleRepository.findByFlyScheduleIdIn(flyScheduleIds);
            Map<Long, Long> countMap = allEmpFlySchedules.stream()
                    .collect(Collectors.groupingBy(
                            efs -> {
                                if (efs.getFlySchedule() != null) {
                                    return efs.getFlySchedule().getFlyScheduleId();
                                }
                                return null;
                            },
                            Collectors.counting()
                    ));
            crewCountMap.putAll(countMap);
        }
        
        final Set<Long> finalAssignedScheduleIds = assignedScheduleIds;
        final Map<Long, Long> finalCrewCountMap = crewCountMap;
        
        return schedules.stream()
                .filter(fs -> fs != null && fs.getFlyScheduleId() != null)
                .map(fs -> convertToListResponse(fs, finalAssignedScheduleIds, finalCrewCountMap))
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FlyScheduleDto.ListResponse> getFlightSchedulesWithAuth(
            Authentication authentication,
            Long airlineId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String departure,
            String destination
    ) {
        String empId = null;
        Long finalAirlineId = airlineId;
        
        if (authentication != null && authentication.isAuthenticated()) {
            String authEmpId = authentication.getName();
            Emp emp = empRepository.findById(authEmpId)
                    .orElseThrow(() -> BusinessException.notFound("존재하지 않는 직원입니다. empId=" + authEmpId));
            
            // ADMIN(AIRLINE_ADMIN, SUPER_ADMIN): 전체 비행편 조회 가능
            // PILOT, CABIN_CREW, MAINTENANCE, GROUND_STAFF: 본인 배정 비행편만 조회
            if (emp.getRole() != CommonEnums.Role.AIRLINE_ADMIN && 
                emp.getRole() != CommonEnums.Role.SUPER_ADMIN) {
                // 일반 직원은 본인 배정 비행편만 조회
                empId = authEmpId;
            } else {
                // 관리자는 자신의 항공사 전체 비행편 조회 가능
                if (finalAirlineId == null && emp.getAirlineId() != null) {
                    finalAirlineId = emp.getAirlineId().getAirlineId();
                }
            }
        }
        
        return getFlightSchedules(finalAirlineId, empId, startDate, endDate, departure, destination);
    }

    @Override
    public FlyScheduleDto getFlightScheduleDetail(Long flyScheduleId, String empId) {
        FlySchedule flySchedule = flyScheduleRepository.findByFlyScheduleId(flyScheduleId)
                .orElseThrow(() -> BusinessException.notFound("해당 비행편이 존재하지 않습니다. flyScheduleId=" + flyScheduleId));
        
        // 크루 멤버 조회 (EmpFlySchedule 사용)
        List<EmpFlySchedule> empFlySchedules = empFlyScheduleRepository.findByFlyScheduleId(flyScheduleId);
        
        List<FlyScheduleDto.CrewMemberResponse> crewMembers = empFlySchedules.stream()
                .map(efs -> {
                    Emp emp = efs.getEmp();
                    if (emp == null) {
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
        
        // 항공사 정보 (FlySchedule의 airlineId 사용)
        Long airlineId = flySchedule.getAirlineId();
        String airlineName = null;
        if (airlineId != null) {
            airlineName = airlineRepository.findById(airlineId)
                    .map(Airline::getAirlineName)
                    .orElse(null);
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

    @Override
    public FlyScheduleDto getFlightScheduleDetailWithAuth(Authentication authentication, Long flyScheduleId) {
        String empId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            empId = authentication.getName();
        }
        
        return getFlightScheduleDetail(flyScheduleId, empId);
    }
    
    private FlyScheduleDto.ListResponse convertToListResponse(FlySchedule fs, Set<Long> assignedScheduleIds, Map<Long, Long> crewCountMap) {
        if (fs == null || fs.getFlyScheduleId() == null) {
            return null;
        }
        
        // 시간 포맷팅 (null 체크)
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
            airlineName = airlineRepository.findById(airlineId)
                    .map(Airline::getAirlineName)
                    .orElse("알 수 없는 항공사");
        } else {
            airlineName = "공통";
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
    
    @Override
    @Transactional
    public void addCrewMemberWithAuth(Authentication authentication, Long flyScheduleId, String empId) {
        // 관리자 권한 체크
        checkAdminPermission(authentication);
        
        addCrewMember(flyScheduleId, empId);
    }

    @Override
    @Transactional
    public void addCrewMember(Long flyScheduleId, String empId) {
        // 비행편 존재 확인 (BoardService 스타일)
        FlySchedule flySchedule = flyScheduleRepository.findByFlyScheduleId(flyScheduleId)
                .orElseThrow(() -> BusinessException.notFound("해당 비행편이 존재하지 않습니다. flyScheduleId=" + flyScheduleId));

        // 직원 존재 확인
        Emp emp = empRepository.findById(empId)
                .orElseThrow(() -> BusinessException.notFound("존재하지 않는 직원입니다. empId=" + empId));

        // 이미 배정되어 있는지 확인
        List<EmpFlySchedule> existing = empFlyScheduleRepository.findByFlyScheduleIdAndEmpId(flyScheduleId, empId);
        if (!existing.isEmpty()) {
            throw BusinessException.conflict("이미 해당 비행편에 배정된 직원입니다.");
        }
        
        // 승무원 배정 생성
        EmpFlySchedule empFlySchedule = EmpFlySchedule.builder()
                .emp(emp)
                .flySchedule(flySchedule)
                .build();
        
        empFlyScheduleRepository.save(empFlySchedule);
    }
    
    @Override
    @Transactional
    public void removeCrewMemberWithAuth(Authentication authentication, Long flyScheduleId, String empId) {
        // 관리자 권한 체크
        checkAdminPermission(authentication);
        
        removeCrewMember(flyScheduleId, empId);
    }

    @Override
    @Transactional
    public void removeCrewMember(Long flyScheduleId, String empId) {
        // 배정 정보 조회
        List<EmpFlySchedule> empFlySchedules = empFlyScheduleRepository.findByFlyScheduleIdAndEmpId(flyScheduleId, empId);
        
        if (empFlySchedules.isEmpty()) {
            throw BusinessException.notFound("해당 비행편에 배정된 직원을 찾을 수 없습니다. flyScheduleId=" + flyScheduleId + ", empId=" + empId);
        }
        
        // 배정 정보 삭제
        empFlyScheduleRepository.deleteAll(empFlySchedules);
    }

    /**
     * 관리자 권한 체크 메서드
     * ADMIN(AIRLINE_ADMIN, SUPER_ADMIN)만 수정 가능
     * BoardService 스타일: 명확한 예외 메시지와 함께 예외 발생
     */
    private void checkAdminPermission(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw BusinessException.forbidden("인증이 필요합니다.");
        }
        
        String authEmpId = authentication.getName();
        Emp emp = empRepository.findById(authEmpId)
                .orElseThrow(() -> BusinessException.notFound("존재하지 않는 직원입니다. empId=" + authEmpId));
        
        // 관리자(AIRLINE_ADMIN, SUPER_ADMIN)만 수정 가능
        // PILOT, CABIN_CREW, MAINTENANCE, GROUND_STAFF는 조회만 가능
        if (emp.getRole() != CommonEnums.Role.AIRLINE_ADMIN && 
            emp.getRole() != CommonEnums.Role.SUPER_ADMIN) {
            throw BusinessException.forbidden("관리자 권한이 필요합니다. 현재 권한: " + emp.getRole());
        }
    }
}
