package com.kh.ct.domain.health.service;

import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.emp.repository.EmpRepository;
import com.kh.ct.domain.health.dto.HealthDto;
import com.kh.ct.domain.health.entity.EmpHealth;
import com.kh.ct.domain.health.entity.EmpPhysicalTest;
import com.kh.ct.domain.health.repository.EmpHealthRepository;
import com.kh.ct.domain.health.repository.HealthRepository;
import com.kh.ct.domain.health.service.parser.HealthLabelParser;
import com.kh.ct.domain.health.service.parser.PdfTextExtractor;
import com.kh.ct.global.entity.File;
import com.kh.ct.global.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class HealthServiceImpl implements HealthService {

    private final HealthRepository healthRepository;
    private final HealthLabelParser healthLabelParser;
    private final PdfTextExtractor pdfTextExtractor;
    private final EmpRepository empRepository;
    private final FileRepository fileRepository;
    private final EmpHealthRepository empHealthRepository;

    private final Path baseDir = Paths.get("uploads", "pdf").toAbsolutePath().normalize();

    @Transactional(readOnly = true)
    @Override
    public HealthDto.PhysicalTestResponse preview(MultipartFile pdfFile) {

        if (pdfFile == null || pdfFile.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String text = pdfTextExtractor.extract(pdfFile);
        System.out.println("TEST" + text);
        HealthDto.PhysicalTestRequest parsed = healthLabelParser.parse(text);
        System.out.println("TEST" + parsed);
        System.out.println("TEST" + parsed.getWeight());



        return HealthDto.PhysicalTestResponse.from(parsed);
    }

    @Transactional
    @Override
    public Long save(MultipartFile pdfFile, String empId, HealthDto.PhysicalTestRequest body) {
        if (pdfFile == null || pdfFile.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        Emp emp = empRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 empId: " + empId));
        System.out.println("TEST!!!!" + body);
        System.out.println("TEST!!!!" + body.getBmi());
        System.out.println("TEST!!!!" + body.getBloodSugar());
        // 1) 파일 디스크 저장 + File row 저장
        String originalName = Optional.ofNullable(pdfFile.getOriginalFilename()).orElse("unknown.pdf");
        long size = pdfFile.getSize();
        String ext = getExt(originalName);
        String storedName = UUID.randomUUID().toString().replace("-", "") + ext;

        Path dir = baseDir;
        Path target = dir.resolve(storedName);

        try {
            Files.createDirectories(dir);
            try (InputStream is = pdfFile.getInputStream()) {
                Files.copy(is, target);
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        File savedFile = fileRepository.save(
                File.builder()
                        .fileOriName(originalName)
                        .fileName(storedName)
                        .path(target.toString())
                        .size(size)
                        .build()
        );


        HealthDto.PhysicalTestRequest req = HealthDto.PhysicalTestRequest.builder()
                .empId(emp)
                .fileId(savedFile)
                .testDate(body.getTestDate())
                .weight(body.getWeight())
                .height(body.getHeight())
                .bloodSugar(body.getBloodSugar())
                .systolicBloodPressure(body.getSystolicBloodPressure())
                .diastolicBloodPressure(body.getDiastolicBloodPressure())
                .cholesterol(body.getCholesterol())
                .heartRate(body.getHeartRate())
                .bmi(body.getBmi())
                .bodyFat(body.getBodyFat())
                .build();

        EmpPhysicalTest saved = healthRepository.save(req.toEntity());
        return saved.getPhysicalTestId();

    }

    @Override
    public HealthDto.PhysicalTestDetailResponse getEmpPhysicalTestById(String empId) {


        Emp emp = empRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 empId: " + empId));

        EmpPhysicalTest test = healthRepository
                .findTopByEmpId_EmpIdOrderByTestDateDesc(empId)
                .orElse(null);

        EmpHealth empHealth = empHealthRepository.findTopByEmpId_EmpIdOrderByEmpHealthIdDesc(empId)
                .orElse(null);


        if (test != null && (test.getEmpId() == null || !empId.equals(test.getEmpId().getEmpId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 사원의 검진 데이터가 아닙니다.");
        }

        return HealthDto.PhysicalTestDetailResponse.builder()
                .empId(empId)
                .empName(emp.getEmpName())
                .startDate(emp.getStartDate())
                .departmentName(emp.getDepartmentId() == null ? null : emp.getDepartmentId().getDepartmentName())
                .job(emp.getJob())
                .email(emp.getEmail())
                .phone(emp.getPhone())
                .address(emp.getAddress())

                .testDate(test == null ? null : test.getTestDate())
                .height(test == null ? null : test.getHeight())
                .weight(test == null ? null : test.getWeight())
                .bloodSugar(test == null ? null : test.getBloodSugar())
                .systolicBloodPressure(test == null ? null : test.getSystolicBloodPressure())
                .diastolicBloodPressure(test == null ? null : test.getDiastolicBloodPressure())
                .cholesterol(test == null ? null : test.getCholesterol())
                .heartRate(test == null ? null : test.getHeartRate())
                .bmi(test == null ? null : test.getBmi())
                .bodyFat(test == null ? null : test.getBodyFat())
                .healthPoint(empHealth == null ? null : empHealth.getHealthPoint())
                .build();
    }

    @Override
    public Page<HealthDto.PhysicalTestResponse> getPhysicalTestByEmpId(String empId, Pageable pageable) {
        Page<EmpPhysicalTest> posts;

        posts = healthRepository.findByEmpId_EmpId(empId,pageable);
        return posts.map(HealthDto.PhysicalTestResponse::from);
    }

    @Override
    public Page<HealthDto.AdminEmpHealthRow> getAllPhysicalTest(String empName,Pageable pageable) {
        System.out.println("pageable = " + pageable);
        
        return empRepository.findAdminEmpHealthRows(empName.trim(), pageable);
    }


    private String getExt(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0) return "";
        return filename.substring(idx); // ".pdf"
    }

}
