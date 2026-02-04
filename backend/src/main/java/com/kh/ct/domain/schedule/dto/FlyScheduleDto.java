package com.kh.ct.domain.schedule.dto;

import com.kh.ct.global.common.CommonEnums;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlyScheduleDto {
    private Long flyScheduleId;
    private String flightNumber;
    private String airplaneType;
    private String departure;
    private LocalDateTime flyStartTime;
    private String destination;
    private LocalDateTime flyEndTime;
    private String gate;
    private Integer crewCount;
    private CommonEnums.flightStatus flightStatus;
    private Integer seatCount;
    private List<FlyScheduleDto.CrewMemberResponse> crewMembers;
    
    // 항공사 정보
    private Long airlineId;
    private String airlineName;
    
    // 시간 포맷팅용 (프론트엔드에서 사용)
    private String departureTime;
    private String arrivalTime;
    private String duration;
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CrewMemberResponse {
        private String empId;
        private String empName;
        private String role;
        private String job;
        private String departmentName;
        private String empStatus;
        private Long empFlyScheduleId;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {
        private Long flyScheduleId;
        private String flightNumber;
        private String departure;
        private String destination;
        private LocalDateTime flyStartTime;
        private LocalDateTime flyEndTime;
        private CommonEnums.flightStatus flightStatus;
        private Integer crewCount;
        private Boolean crewAssigned;
        private Boolean isAssignedToMe;
        private String departureTime;
        private String arrivalTime;
        private String duration;
        private Long airlineId;
        private String airlineName;
    }
}
