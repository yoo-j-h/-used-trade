package com.kh.ct.domain.schedule.controller;

import com.kh.ct.domain.schedule.entity.Airport;
import com.kh.ct.domain.schedule.repository.AirportRepository;
import com.kh.ct.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
public class AirportController {
    
    private final AirportRepository airportRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Airport>>> getAllAirports() {
        List<Airport> airports = airportRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("공항 목록 조회 성공", airports));
    }
}
