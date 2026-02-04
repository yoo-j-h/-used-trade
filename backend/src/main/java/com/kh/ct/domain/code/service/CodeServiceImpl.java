package com.kh.ct.domain.code.service;

import com.kh.ct.domain.code.dto.CodeDetailDto;
import com.kh.ct.domain.code.dto.CodeDto;
import com.kh.ct.domain.code.entity.Code;
import com.kh.ct.domain.code.entity.CodeDetail;
import com.kh.ct.domain.code.repository.CodeDetailRepository;
import com.kh.ct.domain.code.repository.CodeRepository;
import com.kh.ct.domain.emp.entity.Airline;
import com.kh.ct.domain.emp.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import com.kh.ct.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {
    private final CodeRepository codeRepository;
    private final CodeDetailRepository codeDetailRepository;
    private final AirlineRepository airlineRepository;

    @Override
    public List<CodeDto> getCode() {
        List<CodeDto> codes = codeRepository.findCodesWithDetailCount();
        return enrichWithAirlineNames(codes);
    }

    @Override
    public List<CodeDto> getCodeByAirlineId(Long airlineId) {
        List<CodeDto> codes;
        if (airlineId == null) {
            codes = codeRepository.findCodesWithDetailCount();
        } else {
            codes = codeRepository.findCodesWithDetailCountByAirlineId(airlineId);
        }
        return enrichWithAirlineNames(codes);
    }

    private List<CodeDto> enrichWithAirlineNames(List<CodeDto> codes) {
        // 모든 항공사 정보를 한 번에 조회
        Map<Long, String> airlineMap = airlineRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Airline::getAirlineId,
                        Airline::getAirlineName
                ));

        // 각 CodeDto에 airlineName 설정
        codes.forEach(codeDto -> {
            if (codeDto.getAirlineId() != null) {
                String airlineName = airlineMap.get(codeDto.getAirlineId());
                // 항공사 정보가 없을 경우 null 대신 기본값 설정
                codeDto.setAirlineName(airlineName != null ? airlineName : "알 수 없는 항공사");
            } else {
                codeDto.setAirlineName("공통 코드");
            }
        });

        return codes;
    }

    @Override
    public List<CodeDetailDto> getCodeDetails(Long codeId) {
        return codeDetailRepository.findDetailDtosByCodeId(codeId);
    }

    @Override
    @Transactional
    public CodeDto createCode(CodeDto codeDto, Long airlineId) {
        // airlineId가 전달되면 자동 설정 (프론트엔드에서 전달하지 않은 경우)
        Long finalAirlineId = codeDto.getAirlineId() != null ? codeDto.getAirlineId() : airlineId;

        // 중복 검증
        boolean exists;
        if (finalAirlineId != null) {
            exists = codeRepository.existsByCodeNameAndAirlineId(codeDto.getCodeName(), finalAirlineId);
        } else {
            exists = codeRepository.existsByCodeNameAndAirlineIdIsNull(codeDto.getCodeName());
        }

        if (exists) {
            throw BusinessException.conflict("동일한 코드명이 이미 존재합니다. (codeName: " + codeDto.getCodeName() + ")");
        }

        // Code 엔티티 생성
        Code code = Code.builder()
                .codeName(codeDto.getCodeName())
                .airlineId(finalAirlineId)
                .build();

        Code saved = codeRepository.save(code);

        // Response DTO 생성
        CodeDto responseDto = CodeDto.builder()
                .codeId(saved.getCodeId())
                .codeName(saved.getCodeName())
                .count(0L)
                .airlineId(saved.getAirlineId())
                .build();

        // LocalDateTime을 Date로 변환
        if (saved.getCreateDate() != null) {
            responseDto.setCreateDate(java.util.Date.from(saved.getCreateDate().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        }
        if (saved.getUpdateDate() != null) {
            responseDto.setUpdateDate(java.util.Date.from(saved.getUpdateDate().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        }

        return responseDto;
    }

    @Override
    @Transactional
    public CodeDetailDto createCodeDetail(Long codeId, CodeDetailDto codeDetailDto) {
        // Code 존재 여부 확인
        Code code = codeRepository.findById(codeId)
                .orElseThrow(() -> BusinessException.notFound("코드를 찾을 수 없습니다. (codeId: " + codeId + ")"));

        // 중복 검증
        if (codeDetailRepository.existsByCodeIdAndCodeDetailName(code, codeDetailDto.getCodeDetailName())) {
            throw BusinessException.conflict("동일한 코드 디테일명이 이미 존재합니다. (detailName: " + codeDetailDto.getCodeDetailName() + ")");
        }

        // CodeDetail 엔티티 생성
        CodeDetail codeDetail = CodeDetail.builder()
                .codeId(code)
                .codeDetailName(codeDetailDto.getCodeDetailName())
                .codeDesc(codeDetailDto.getCodeDesc())
                .build();

        CodeDetail saved = codeDetailRepository.save(codeDetail);

        // Response DTO 생성
        CodeDetailDto responseDto = CodeDetailDto.builder()
                .codeDetailId(saved.getCodeDetailId())
                .codeId(saved.getCodeId().getCodeId())
                .codeDetailName(saved.getCodeDetailName())
                .codeDesc(saved.getCodeDesc())
                .build();

        // LocalDateTime을 Date로 변환
        if (saved.getCreateDate() != null) {
            responseDto.setCreateDate(java.util.Date.from(saved.getCreateDate().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        }
        if (saved.getUpdateDate() != null) {
            responseDto.setUpdateDate(java.util.Date.from(saved.getUpdateDate().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        }

        return responseDto;
    }

    @Override
    @Transactional
    public void deleteCode(Long codeId) {
        // Code 존재 여부 확인
        Code code = codeRepository.findById(codeId)
                .orElseThrow(() -> BusinessException.notFound("코드를 찾을 수 없습니다. (codeId: " + codeId + ")"));

        // Code 삭제 (Cascade로 인해 CodeDetail도 함께 삭제됨)
        codeRepository.delete(code);
    }

    @Override
    @Transactional
    public CodeDetailDto updateCodeDetail(Long codeId, Long codeDetailId, CodeDetailDto codeDetailDto) {
        // Code 존재 여부 확인
        Code code = codeRepository.findById(codeId)
                .orElseThrow(() -> BusinessException.notFound("코드를 찾을 수 없습니다. (codeId: " + codeId + ")"));

        // CodeDetail 존재 여부 확인
        CodeDetail codeDetail = codeDetailRepository.findByCodeDetailIdAndCodeId_CodeId(codeDetailId, codeId)
                .orElseThrow(() -> BusinessException.notFound("코드 디테일을 찾을 수 없습니다. (codeDetailId: " + codeDetailId + ")"));

        // 중복 검증 (자기 자신 제외)
        if (!codeDetail.getCodeDetailName().equals(codeDetailDto.getCodeDetailName())) {
            if (codeDetailRepository.existsByCodeIdAndCodeDetailName(code, codeDetailDto.getCodeDetailName())) {
                throw BusinessException.conflict("동일한 코드 디테일명이 이미 존재합니다. (detailName: " + codeDetailDto.getCodeDetailName() + ")");
            }
        }

        // CodeDetail 엔티티 수정
        codeDetail = CodeDetail.builder()
                .codeDetailId(codeDetail.getCodeDetailId())
                .codeId(code)
                .codeDetailName(codeDetailDto.getCodeDetailName())
                .codeDesc(codeDetailDto.getCodeDesc())
                .build();

        CodeDetail saved = codeDetailRepository.save(codeDetail);

        // Response DTO 생성
        CodeDetailDto responseDto = CodeDetailDto.builder()
                .codeDetailId(saved.getCodeDetailId())
                .codeId(saved.getCodeId().getCodeId())
                .codeDetailName(saved.getCodeDetailName())
                .codeDesc(saved.getCodeDesc())
                .build();

        // LocalDateTime을 Date로 변환
        if (saved.getCreateDate() != null) {
            responseDto.setCreateDate(java.util.Date.from(saved.getCreateDate().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        }
        if (saved.getUpdateDate() != null) {
            responseDto.setUpdateDate(java.util.Date.from(saved.getUpdateDate().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        }

        return responseDto;
    }

    @Override
    @Transactional
    public void deleteCodeDetail(Long codeId, Long codeDetailId) {
        // Code 존재 여부 확인
        Code code = codeRepository.findById(codeId)
                .orElseThrow(() -> BusinessException.notFound("코드를 찾을 수 없습니다. (codeId: " + codeId + ")"));

        // CodeDetail 존재 여부 확인
        CodeDetail codeDetail = codeDetailRepository.findByCodeDetailIdAndCodeId_CodeId(codeDetailId, codeId)
                .orElseThrow(() -> BusinessException.notFound("코드 디테일을 찾을 수 없습니다. (codeDetailId: " + codeDetailId + ")"));

        // CodeDetail 삭제
        codeDetailRepository.delete(codeDetail);
    }
}

