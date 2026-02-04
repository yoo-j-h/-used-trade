package com.kh.ct.domain.code.dto;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeDetailDto {
    private Long codeDetailId;
    private Long codeId;
    private String codeDetailName;
    private String codeDesc;
    private Date createDate;
    private Date updateDate;


    // JPQL 쿼리용 생성자 (codeId 포함)
    public CodeDetailDto(Long codeDetailId, Long codeId, String codeDetailName, String codeDesc) {
        this.codeDetailId = codeDetailId;
        this.codeId = codeId;
        this.codeDetailName = codeDetailName;
        this.codeDesc = codeDesc;
    }
}

