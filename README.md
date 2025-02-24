# 배달 및 포장 음식 주문 관리 플랫폼

## 💡 개요
이 프로젝트는 **광화문 근처**에서 운영될 음식점들을 위한 **배달 및 포장 음식 주문 관리 플랫폼**을 개발하는 것을 목표로 합니다.  
플랫폼은 음식점의 주문 관리, 결제 처리, 주문 내역 관리 기능을 제공하며, 향후 배달(라이더 할당, 배송 상태 관리) 기능은 별도 개발 예정입니다.

---

## 🤝 팀원 역할분담
- **PM/백엔드 개발**: [이름] - 프로젝트 기획 및 백엔드 개발 총괄
- **백엔드 개발**: [이름] - 주문 및 결제 기능 개발
- **프론트엔드 개발**: [이름] - UI/UX 및 프론트엔드 개발
- **데브옵스**: [이름] - 클라우드 배포 및 서버 관리

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

## 📊 ERD (데이터베이스 설계)
```txt
[사용자]
- id (PK)
- username
- password
- role (CUSTOMER, OWNER, MANAGER, MASTER)
- created_at, updated_at

[가게]
- id (PK)
- name
- owner_id (FK - 사용자)
- category_id (FK - 카테고리)
- legal_code
- content
- operating_hours
- created_at, updated_at, deleted_at

[상품]
- id (PK)
- restaurant_id (FK - 가게)
- name
- price
- description
- created_at, updated_at

[주문]
- id (PK)
- customer_id (FK - 사용자)
- restaurant_id (FK - 가게)
- status (PENDING, CONFIRMED, CANCELLED)
- created_at, updated_at

[결제]
- id (PK)
- order_id (FK - 주문)
- amount
- payment_method
- transaction_id
- created_at, updated_at

[리뷰]
- id (PK)
- order_id (FK - 주문)
- rating (1~5)
- comment
- created_at, updated_at

[AI 요청 로그]
- id (PK)
- request_text
- response_text
- created_at
```

---

## 🛠 기술 스택
- **백엔드**: Java 17, Spring Boot 3.x, Spring Security, JPA, QueryDSL
- **데이터베이스**: PostgreSQL, Redis (세션 관리 및 캐싱)
- **클라우드**: AWS / Oracle Cloud (배포)
- **API 문서화**: Swagger, Spring RestDocs
- **CI/CD**: GitHub Actions, Docker, Kubernetes (선택 사항)
- **테스트**: JUnit5, Mockito, RestAssured

---

## 📌 API 문서
API 명세서는 Swagger 및 RestDocs를 통해 제공됩니다.
- **Swagger URL:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **RestDocs:** `docs/api-guide.html`

---

## 🔍 추가 고려 사항
- **PG사 연동**: 결제 관련 정보는 외부 PG사 API와 연동하여 관리
- **데이터 보존 정책**: 데이터는 숨김 처리, 삭제일/삭제자 기록 유지
- **보안**: JWT 기반 인증, Spring Security 활용한 권한 관리
- **확장성 고려**: 지점 추가, 지역 카테고리 확장 가능하도록 설계

