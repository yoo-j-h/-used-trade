# 🛒 우동마켓(Udong Market)
React + Spring Boot(JPA) 기반의 REST 중고거래 서비스

우동마켓은 사용자가 **회원가입 → 게시글(상품) 등록 → 댓글 작성**까지  
**실제 중고거래 서비스와 동일한 흐름**을 경험할 수 있도록 설계된 웹 애플리케이션입니다.  
프론트엔드는 React, 백엔드는 Spring Boot 기반 **REST API 서버**로 구현했으며,  
API는 **DTO 기반 설계(Entity 직접 반환 금지)** 원칙을 따릅니다.

---

## 📚 목차
- [프로젝트 소개](#-프로젝트-소개)
- [기술 스택](#-기술-스택)
- [주요 도메인 설명](#-주요-도메인-설명)
- [API 명세](#-api-명세)
- [실행 방법](#-실행-방법)

---

## 📝 프로젝트 소개

우동마켓은 React 과제에서 필요한 기능을 백엔드에서 제공하는 **REST API 서버**로 구현한 중고거래 플랫폼입니다.

- **JPA + JPQL(EntityManager)** 기반 CRUD 구현
- HTTP 메서드/URI/응답 구조를 직접 설계하여 REST 감각 학습
- **Entity 직접 반환 금지**, DTO 기반 API 설계 적용

---

## 🛠 기술 스택

### Backend
- Spring Boot 3.x
- Spring Web
- JPA (EntityManager + JPQL)
- H2 Database
- Lombok
- Java 17

### Frontend
- React
- React Router
- Styled-components
- Axios
- Vite

---

## 🧩 주요 도메인 설명

- **Member(회원)**: 가입/조회/수정/삭제(소프트 삭제)
- **Board(게시글/상품)**: 상품 정보 등록/조회/수정/삭제
- **Reply(댓글)**: 게시글에 대한 댓글 CRUD (삭제는 소프트 삭제)
- **Profile(프로필)**: 회원의 프로필 정보(1:1)

### 연관관계
- Member : Board = 1 : N
- Board : Reply = 1 : N
- Member : Reply = 1 : N
- Member : Profile = 1 : 1

### 삭제 정책
- Member: 소프트 삭제(`status = N`), 조회 시 `status = Y`만 노출
- Board: 하드 삭제
- Reply: 소프트 삭제(`status = N`), 조회 시 `status = Y`만 노출

---

## 📌 API 명세

> Base URL: `http://localhost:8888`

### 1) Member API (`/api/member`)

| Method | URL | 설명 |
|---|---|---|
| POST | `/api/member` | 회원가입 |
| GET | `/api/member` | 회원 전체 조회 (status=Y) |
| GET | `/api/member/{userId}` | 회원 단건 조회 (status=Y) |
| PUT | `/api/member/{userId}` | 회원 정보 수정 |
| DELETE | `/api/member/{userId}` | 회원 삭제 (소프트 삭제) |
| GET | `/api/member/search?keyword={keyword}` | 회원 이름 검색 (status=Y) |

#### 회원가입 예시
**Request**
json
{
  "user_id": "user01",
  "user_pwd": "1234",
  "user_name": "홍길동",
  "email": "user01@test.com",
  "phone": "010-1234-5678",
  "address": "서울 강남구 역삼동"
}
Response (200 OK)

json
코드 복사
"user01"
2) Board API (/api/board)
Method	URL	설명
POST	/api/board	게시글 등록
GET	/api/board	게시글 목록 조회 (Page)
GET	/api/board/{boardId}	게시글 상세 조회 (댓글 포함)
PATCH	/api/board/{boardId}	게시글 수정
DELETE	/api/board/{boardId}	게시글 삭제 (하드 삭제)

게시글 등록 예시
Request

json
코드 복사
{
  "board_title": "아이폰 13 판매합니다",
  "board_content": "상태 좋습니다.",
  "category": "전자기기",
  "price": 500000,
  "sale_status": "판매중",
  "image_url": "/images/iphone13.png",
  "region": "서울 강남구",
  "user_id": "user01"
}
Response (200 OK)

json
코드 복사
1
3) Reply API
Method	URL	설명
POST	/api/boards/{boardId}/replies	댓글 등록
GET	/api/boards/{boardId}/replies	댓글 목록 조회 (status=Y)
PATCH	/api/replies/{replyNo}	댓글 수정
DELETE	/api/replies/{replyNo}	댓글 삭제 (소프트 삭제)

댓글 등록 예시
Request

json
코드 복사
{
  "user_id": "user02",
  "reply_content": "구매 가능할까요?"
}
Response (200 OK)

json
코드 복사
10
🚀 실행 방법
1) 백엔드 실행 (Spring Boot)
bash
코드 복사
cd back/jpa/jpa
./gradlew bootRun
서버 실행 주소: http://localhost:8888

2) H2 콘솔 접속 정보
URL: http://localhost:8888/h2-console

Driver: org.h2.Driver

JDBC URL: jdbc:h2:tcp://localhost/C:\workspace\07_RestServer\jpa/tdb

Username: sa

Password: 1234

3) 프론트엔드 실행 (React)
bash
코드 복사
cd front
npm install
npm run dev
프론트 실행 주소: http://localhost:5173
