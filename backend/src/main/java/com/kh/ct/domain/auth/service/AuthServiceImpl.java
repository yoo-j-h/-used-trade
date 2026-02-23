package com.kh.ct.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.ct.domain.auth.dto.AuthDto;
import com.kh.ct.domain.emp.entity.Emp;
import com.kh.ct.domain.emp.repository.EmpRepository;
import com.kh.ct.global.common.CommonEnums;
import com.kh.ct.global.exception.BusinessException;
import com.kh.ct.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final EmpRepository empRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatClient.Builder chatClientBuilder;
    private final ObjectMapper objectMapper;

    // ──────────────────────────────────────────────
    // 로그인 / 내 정보 조회
    // ──────────────────────────────────────────────

    @Override
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {

        // 1) 회원 조회
        Emp emp = empRepository.findById(request.getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("아이디나 비밀번호가 일치하지 않습니다."));

        // 2) 비밀번호 검증
        if (!passwordEncoder.matches(request.getEmpPwd(), emp.getEmpPwd())) {
            throw new IllegalArgumentException("아이디나 비밀번호가 일치하지 않습니다.");
        }

        // 3) 토큰 발급
        String token = jwtTokenProvider.generateToken(emp.getEmpId(), emp.getRole().name());

        return AuthDto.LoginResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthDto.MeResponse me(String empId) {
        Emp emp = empRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + empId));

        return AuthDto.MeResponse.builder()
                .empId(emp.getEmpId())
                .empName(emp.getEmpName())
                .role(emp.getRole().name())
                .airlineId(emp.getAirlineId() != null ? emp.getAirlineId().getAirlineId() : null)
                .build();
    }

    // ──────────────────────────────────────────────
    // 명함 OCR - DB 저장 없음, 폼 자동완성용
    // ──────────────────────────────────────────────

    @Override
    public AuthDto.BusinessCardOcrResponse extractBusinessCard(MultipartFile file) {
        log.info("명함 OCR 시작 - 파일명: {}", file.getOriginalFilename());

        try {
            // 1. MultipartFile → ByteArrayResource 변환
            byte[] fileBytes = file.getBytes();
            ByteArrayResource resource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            // 2. Gemini 프롬프트 구성
            String promptText = "이 명함 이미지에서 다음 정보를 추출해서 순수 JSON 형식으로만 반환해. " +
                    "마크다운(```json)이나 부가 설명은 절대 넣지 마.\n" +
                    "- empName: 이름 (필수)\n" +
                    "- phone: 전화번호 (숫자와 하이픈만 포함, 국가코드 제거 후 010-xxxx-xxxx 형태로, 없으면 null)\n" +
                    "- email: 이메일 (없으면 null)\n" +
                    "- job: 직급 또는 직책 (없으면 null)\n" +
                    "- company: 회사명 또는 소속 (없으면 null)\n" +
                    "- address: 주소 (도로명 주소 또는 지번 주소, 없으면 null)\n" +
                    "응답 예시: {\"empName\":\"홍길동\",\"phone\":\"010-1234-5678\",\"email\":\"hong@example.com\",\"job\":\"과장\",\"company\":\"한국항공\",\"address\":\"서울특별시 강서구 공항대로 456\"}";

            // 3. ChatClient 호출
            ChatClient chatClient = chatClientBuilder.build();
            String rawResponse = chatClient.prompt()
                    .user(userSpec -> userSpec
                            .text(promptText)
                            .media(MimeTypeUtils.parseMimeType(file.getContentType()), resource))
                    .call()
                    .content();

            // 4. 마크다운 코드 블록 제거
            String cleanedJson = removeMarkdownCodeBlocks(rawResponse);
            log.info("명함 OCR LLM 응답: {}", cleanedJson);

            // 5. JSON → DTO 파싱
            try {
                AuthDto.BusinessCardOcrResponse result = objectMapper.readValue(cleanedJson,
                        AuthDto.BusinessCardOcrResponse.class);
                log.info("명함 OCR 완료 - 이름: {}, 연락처: {}", result.getEmpName(), result.getPhone());
                return result;
            } catch (JsonProcessingException e) {
                log.error("명함 OCR JSON 파싱 실패 - 응답: {}", cleanedJson, e);
                throw BusinessException.badRequest(
                        "명함 정보 파싱에 실패했습니다. AI가 올바른 JSON 형식으로 응답하지 않았습니다.");
            }

        } catch (IOException e) {
            log.error("명함 이미지 파일 읽기 실패", e);
            throw BusinessException.badRequest("파일을 읽을 수 없습니다: " + e.getMessage());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("명함 OCR 처리 중 오류 발생", e);
            throw BusinessException.badRequest("명함 OCR 처리에 실패했습니다: " + e.getMessage());
        }
    }

    // ──────────────────────────────────────────────
    // 회원가입 - DB 저장
    // ──────────────────────────────────────────────

    @Override
    @Transactional
    public void signUp(AuthDto.SignUpRequest request) {
        log.info("회원가입 요청 - empId: {}, empNo: {}", request.getEmpId(), request.getEmpNo());

        // 1. empId 중복 검사
        if (empRepository.existsById(request.getEmpId())) {
            throw BusinessException.conflict("이미 사용 중인 아이디입니다: " + request.getEmpId());
        }

        // 2. empNo 중복 검사
        if (empRepository.existsByEmpNo(request.getEmpNo())) {
            throw BusinessException.conflict("이미 사용 중인 사번입니다: " + request.getEmpNo());
        }

        // 3. 비밀번호 암호화
        String encodedPwd = passwordEncoder.encode(request.getEmpPwd());

        // 4. Emp 엔티티 빌드 및 저장
        Emp emp = Emp.builder()
                .empId(request.getEmpId())
                .empPwd(encodedPwd)
                .empName(request.getEmpName())
                .age(request.getAge())
                .role(request.getRole())
                .phone(request.getPhone())
                .email(request.getEmail())
                .job(request.getJob())
                .empNo(request.getEmpNo())
                .empStatus(CommonEnums.EmpStatus.Y) // 기본값: 재직
                .startDate(LocalDateTime.now())
                .leaveCount(0.0)
                .build();

        empRepository.save(emp);
        log.info("회원가입 완료 - empId: {}, empName: {}", emp.getEmpId(), emp.getEmpName());
    }

    // ──────────────────────────────────────────────
    // 내부 유틸리티
    // ──────────────────────────────────────────────

    /**
     * LLM 응답에서 마크다운 코드 블록(```json ... ```)을 제거합니다.
     */
    private String removeMarkdownCodeBlocks(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        String cleaned = text.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim();
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3).trim();
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
        }
        return cleaned;
    }
}
