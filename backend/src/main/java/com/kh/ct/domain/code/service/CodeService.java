package com.kh.ct.domain.code.service;

import com.kh.ct.domain.code.dto.CodeDetailDto;
import com.kh.ct.domain.code.dto.CodeDto;

import java.util.List;

public interface CodeService {

    //코드 조회
    List<CodeDto> getCode();
    List<CodeDto> getCodeByAirlineId(Long airlineId);
    List<CodeDetailDto> getCodeDetails(Long codeId);

    //코드등록
    CodeDto createCode(CodeDto codeDto, Long airlineId);

    //코드 디테일 등록
    CodeDetailDto createCodeDetail(Long codeId, CodeDetailDto codeDetailDto);

    //코드 그룹 삭제
    void deleteCode(Long codeId);

    //코드 디테일 수정
    CodeDetailDto updateCodeDetail(Long codeId, Long codeDetailId, CodeDetailDto codeDetailDto);

    //코드 디테일 삭제
    void deleteCodeDetail(Long codeId, Long codeDetailId);

}
