# 배달 및 포장 음식 주문 관리 플랫폼

## 💡 개요

이 프로젝트는 **광화문 근처**에서 운영될 음식점들을 위한 **배달 및 포장 음식 주문 관리 플랫폼**을 개발하는 것을 목표로 합니다.
플랫폼은 음식점의 주문 관리, 결제 처리, 주문 내역 관리 기능을 제공하며, 향후 배달(라이더 할당, 배송 상태 관리) 기능은 별도 개발 예정입니다.

---
## 🎯 프로젝트 목적/상세
### 1. 주요 기능
1. **주문 관리**
   - 음식점에서 음식 정보를 등록하고, 고객은 원하는 음식을 주문
   - 주문 내역 및 결제 정보 관리, 주문 취소는 5분 이내 가능
   - 온라인 주문 및 대면 주문(매장 주문) 지원

2. **결제 시스템**
   - 카드 결제 지원, PG사와 연동된 결제 내역 관리

3. **데이터 관리**
   - 모든 데이터는 완전 삭제 대신 숨김 처리
   - 생성일, 생성자, 수정일, 수정자, 삭제일, 삭제자 기록

4. **접근 권한 관리**
   - 고객: 자신의 주문 내역만 조회
   - 가게 주인: 자신의 가게 주문 및 메뉴 관리 가능
   - 관리자: 모든 가게 및 주문 관리 가능

5. **배송지 정보 관리**
   - 주문 및 배달 시 필수 입력 사항

6. **AI API 연동**
   - **상품 설명 자동 생성** (Google Cloud Generative Language API 활용)
   - **AI 요청 기록** 및 데이터 저장
   - **사용량 최적화**: 입력 텍스트 제한 및 간결한 응답 요청

7. **클라우드 서비스 배포**
   - AWS 또는 Oracle Cloud 프리 티어 사용
   - 배포 방식: Docker 컨테이너 및 CI/CD 구축

8. **Spring Security 고도화**
   - 관리자만 가게 추가 가능하도록 접근 제어

9. **리뷰 및 평점 기능**
   - 주문을 기반으로 음식점 리뷰 및 평점 저장
   - 음식점 검색 시 리뷰 노출 및 평점 1~5점 계산
---
## 🚀 주요 기능

### 1. 모든 도메인의 CRUD 및 Search 기능

- **각 도메인 (사용자, 가게, 상품, 주문, 결제, 리뷰/평점, AI 요청 로그)**
  - CRUD 구현
  - 검색 기능 포함: 기본 검색 조건 및 정렬 기능 (생성일, 수정일 순)
  - 페이지당 노출 건수: 기본 10건, 옵션으로 30건, 50건 선택 (그 외는 기본 10건으로 고정)
- **접근 권한 관리**
  - 고객: 자신의 주문 내역만 조회 가능
  - 가게 주인: 자신의 가게 주문 내역, 가게 정보, 주문 처리 및 메뉴 수정 가능
  - 관리자: 모든 가게 및 주문에 대한 전체 권한 보유

### 2. 사용자 인증 기능

- **회원가입** (`POST /api/auth/signup`)

  - username: `최소 4자 이상, 10자 이하`이며 `소문자(a~z), 숫자(0~9)`로 구성
  - password: `최소 8자 이상, 15자 이하`이며 `알파벳 대소문자, 숫자, 특수문자` 포함
  - 사용자 권한: `CUSTOMER`, `OWNER`, `MANAGER`, `MASTER`

- **로그인 및 로그아웃**

  - JWT 기반 인증 (Access Token, Refresh Token 발급 및 관리)
  - API 엔드포인트: `POST /api/auth/login`, `POST /api/auth/logout`

### 3. 결제 시스템

- **카드 결제만 지원** (PG사 연동을 통해 결제 정보 저장)
- **결제 테이블 운영** (결제 내역 관리)

### 4. 주문 관리

- **주문 생성 후 5분 이내 취소 가능**
- **온라인 주문 & 대면 주문 지원** (가게에서 직접 주문 가능)
- **주문 상태 관리** (진행 중, 완료 등 단계별 상태 업데이트)

### 5. 배송지 정보 관리

- **배송지 정보 (주소, 요청사항) 필수 입력**
- **배송 관련 추가 기능 개발 예정**

### 6. AI API 연동

- **상품 설명 자동 생성**
  - Google Cloud Generative Language API 연동 (`Gemini-1.5 Flash Latest 모델` 활용)
  - 음식점 사장님이 상품 설명을 쉽게 작성할 수 있도록 지원
- **AI 요청 기록**
  - AI API 요청과 응답 문장을 DB에 저장
  - 요청 글자 수 제한 + 응답을 간결하게 50자 이하로 요청하여 최적화

### 7. 클라우드 서비스 배포

- AWS 또는 Oracle Cloud 프리 티어 활용하여 실제 서비스 배포
- 배포 방식: 파일 업로드, Docker 컨테이너 등 다양한 방식 지원

### 8. Spring Security 고도화

- **관리자 권한 설정**
  - 가게 추가 시 `관리자` 권한 사용자만 가능

### 9. 리뷰 및 평점 기능

- **주문 기반 리뷰 및 평점 저장** (1\~5점 평점 시스템 운영)
- **가게 목록 조회 시 평점 계산 및 노출** (N+1 문제 고려)
- **주문 검색 시 음식점 리뷰 리스트 표시**

### 10. API 문서화

- **Swagger, RestDoc을 활용한 API 문서화**

### 11. 테스트 코드 작성

- **각 도메인 주요 API에 대해 통합 테스트 작성** (성공/실패 케이스 포함)

### 12. QueryDSL 구현

- **지점 검색 시 카테고리 & 지점 이름 복합 필터링**

---

## 📌 추가 고려 사항

### ✅ 데이터 관리

- **완전 삭제 대신 숨김 처리 방식 적용**
- **감사 로그 기록** (생성일, 수정일, 삭제일 정보 포함)

### ✅ 확장성 고려

- **지역 및 카테고리 확장 가능하도록 설계**

### ✅ 보안 강화

- **Spring Security를 활용한 접근 권한 관리**

---

## 🚀 서비스 구성 및 실행 방법

### 1. 환경 설정
- **Java 17**
- **Spring Boot 3.x**
- **Gradle**
- **Docker** (선택)
- **AWS 또는 Oracle Cloud 프리 티어** (배포 환경)

### 2. 실행 방법
```sh
# 프로젝트 클론
git clone https://github.com/example/delivery-management.git
cd delivery-management

# 환경변수 설정
cp .env.example .env

# 빌드 및 실행
./gradlew bootRun
```

---

### 1. 환경 설정

```bash
# 프로젝트 클론
$ git clone https://github.com/example/repository.git
$ cd repository

# 환경 변수 설정 (.env 사용 가능)
$ touch .env

# 빌드 및 실행
$ ./gradlew build
$ java -jar build/libs/app.jar
```

### 2. API 문서 확인

- Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Docker 컨테이너 실행

```bash
$ docker build -t order-management .
$ docker run -p 8080:8080 order-management
```

---

## 🏗 기술 스택

| 분야           | 기술                                          |
| ------------ | ------------------------------------------- |
| **Backend**  | Spring Boot, Spring Security, JPA, QueryDSL |
| **Database** | PostgreSQL, Redis (캐싱)                      |
| **Cloud**    | AWS EC2, Oracle Cloud (프리티어)                |
| **CI/CD**    | GitHub Actions, Docker                      |
| **API Docs** | Swagger, RestDocs                           |
| **AI 연동**    | Google Generative Language API (Gemini-1.5) |

---

## 📜 ERD

(ERD 이미지 삽입)

---

## 🎯 팀원 역할분담

| 역할             | 담당자      |
| -------------- | -------- |
| **백엔드 개발**     | 김개발, 이엔지 |
| **프론트엔드 개발**   | 박디자인     |
| **DB 설계 및 관리** | 정DB      |
| **AI API 연동**  | 최AI      |
| **클라우드 배포**    | 유클라우드    |

---

## 🔗 참고 링크

- [Google AI API 문서](https://aistudio.google.com/)
- [Spring Security 공식 문서](https://spring.io/projects/spring-security)
- [QueryDSL 공식 문서](https://querydsl.com/)

