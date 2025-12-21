# 🛒 우동마켓(Udong Market)
React + Spring Boot(JPA) 기반의 REST 중고거래 서비스

우동마켓은 사용자가 **회원가입 → 게시글(상품) 등록 → 댓글 작성**까지  
**실제 중고거래 서비스와 동일한 흐름**을 경험할 수 있도록 설계된 웹 애플리케이션입니다.  
프론트엔드는 React, 백엔드는 Spring Boot 기반 **REST API 서버**로 구현했으며,  
API는 **DTO 기반 설계(Entity 직접 반환 금지)** 원칙을 따릅니다.

---

## 📚 목차
- [프로젝트 소개](#-프로젝트-소개)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [프로젝트 구조](#-프로젝트-구조)
- [설계 철학](#-설계-철학)
- [도메인 모델](#-도메인-모델)
- [API 문서](#-api-문서)
- [실행 방법](#-실행-방법)
- [개발 환경](#-개발-환경)
- [향후 확장 계획](#-향후-확장-계획)

---

## 📝 프로젝트 소개

우동마켓은 **React + Spring Boot REST API** 기반의 중고거래 플랫폼입니다.

- React에서 필요한 기능을 Spring Boot 백엔드가 REST API로 제공
- DB는 H2를 사용하여 개발/테스트 환경을 단순화
- JPA + JPQL(EntityManager) 기반으로 CRUD 구현
- **DTO 기반 응답**을 통해 REST 설계 감각을 익히는 것이 목적

---

## ✨ 주요 기능

### 👤 회원 기능
- 회원가입
- 회원 조회(전체/단건/이름 검색)
- 회원 정보 수정
- 회원 삭제(소프트 삭제: `status = N`)
  - 조회 시 활성 회원(`status = Y`)만 노출

---

### 🛒 게시글(상품) 기능
- 게시글 등록
- 게시글 목록 조회 (페이지네이션)
- 게시글 상세 조회 (댓글 포함)
- 게시글 수정
- 게시글 삭제(하드 삭제)

> 이미지 데이터는 Base64로 저장하지 않고 **이미지 URL 문자열**을 저장합니다.

---

### 💬 댓글 기능
- 댓글 등록
- 댓글 목록 조회(게시글 기준, 활성 댓글만)
- 댓글 수정
- 댓글 삭제(소프트 삭제: `status = N`)

---

## 🛠 기술 스택

### Frontend
- React
- React Router
- Styled-components
- Axios
- Vite

### Backend
- Spring Boot 3.x
- Spring Web (REST API)
- JPA (EntityManager + JPQL)
- Lombok
- Java 17

### Database
- H2 Database

---

## 📁 프로젝트 구조

```text
udongmarket-rest/
├── backend/
│   ├── src/main/java/com/kh/jpa/
│   │   ├── controller/
│   │   │   ├── MemberController.java
│   │   │   ├── BoardController.java
│   │   │   └── ReplyController.java
│   │   ├── service/
│   │   │   ├── MemberService.java
│   │   │   ├── MemberServiceImpl.java
│   │   │   ├── BoardService.java
│   │   │   ├── BoardServiceImpl.java
│   │   │   ├── ReplyService.java
│   │   │   └── ReplyServiceImpl.java
│   │   ├── repository/
│   │   │   ├── MemberRepository.java
│   │   │   ├── MemberRepositoryImpl.java
│   │   │   ├── BoardRepository.java
│   │   │   └── BoardRepositoryImpl.java
│   │   ├── entity/
│   │   │   ├── BaseTimeEntity.java
│   │   │   ├── Member.java
│   │   │   ├── Profile.java
│   │   │   ├── Board.java
│   │   │   └── Reply.java
│   │   ├── dto/
│   │   │   ├── MemberDto.java
│   │   │   ├── BoardDto.java
│   │   │   ├── ReplyDto.java
│   │   │   └── PageResponse.java
│   │   ├── enums/
│   │   │   └── CommonEnums.java
│   │   └── JpaApplication.java
│   └── src/main/resources/
│       └── application.yml
│
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

yaml
코드 복사

---

## 🧱 설계 철학

### 1) RESTful 설계 원칙
- URI는 리소스를 의미하도록 설계 (복수형 권장)
- HTTP 메서드로 행위 표현
- Request/Response는 JSON 기반

### 2) DTO 기반 API 설계 (Entity 직접 반환 금지)
- Entity는 DB 모델
- DTO는 API 계약(Contract)
- Controller는 DTO로 요청을 받고 DTO로 응답

### 3) JPA + JPQL로 CRUD 구현
- 단순한 Spring Data 메서드가 아니라
- JPQL을 직접 사용해 조회 로직을 구현하여 JPA 이해도를 높임

### 4) 삭제 정책 분리
- Member: 소프트 삭제(`status = N`)
- Board: 하드 삭제(필요 시 실제 삭제)
- Reply: 소프트 삭제(`status = N`)

---

## 🧩 도메인 모델

- Member : Board = 1 : N
- Board : Reply = 1 : N
- Member : Reply = 1 : N
- Member : Profile = 1 : 1

---

## 📌 API 문서

> Base URL: `http://localhost:8888`

### 👤 Member API (`/api/member`)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/member` | 회원가입 |
| GET | `/api/member` | 회원 전체 조회 (status=Y) |
| GET | `/api/member/{userId}` | 회원 단건 조회 (status=Y) |
| PUT | `/api/member/{userId}` | 회원 정보 수정 |
| DELETE | `/api/member/{userId}` | 회원 삭제 (소프트 삭제) |
| GET | `/api/member/search?keyword={keyword}` | 회원 이름 검색 (status=Y) |

### 🛒 Board API (`/api/board`)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/board` | 게시글 등록 |
| GET | `/api/board` | 게시글 목록 조회 (Page) |
| GET | `/api/board/{boardId}` | 게시글 상세 조회 (댓글 포함) |
| PATCH | `/api/board/{boardId}` | 게시글 수정 |
| DELETE | `/api/board/{boardId}` | 게시글 삭제 (하드 삭제) |

### 💬 Reply API
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/boards/{boardId}/replies` | 댓글 등록 |
| GET | `/api/boards/{boardId}/replies` | 댓글 목록 조회 (status=Y) |
| PATCH | `/api/replies/{replyNo}` | 댓글 수정 |
| DELETE | `/api/replies/{replyNo}` | 댓글 삭제 (소프트 삭제) |

---

## 🚀 실행 방법

### 사전 요구사항
- Java 17 이상
- Node.js 18 이상
- npm 또는 yarn

### 1) 백엔드 실행
```bash
cd backend
./gradlew bootRun
백엔드 서버: http://localhost:8888

2) 프론트엔드 실행
bash
코드 복사
cd frontend
npm install
npm run dev
프론트엔드 서버: http://localhost:5173
