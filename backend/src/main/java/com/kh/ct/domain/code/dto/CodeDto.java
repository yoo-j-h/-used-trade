package com.kh.ct.domain.code.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeDto {
    private Long codeId;
    private String codeName;
    private Long count;
    private Date createDate;
    private Date updateDate;
    private Long airlineId;
    private String airlineName;

    public CodeDto(Long codeId, String codeName, Long count, Long airlineId) {
        this.codeId = codeId;
        this.codeName = codeName;
        this.count = count;
        this.airlineId = airlineId;
    }
}
