# Lunch_Match 프로젝트 개발 현황 및 계획 (yyj)

## 1. 개발 환경 준비
- 스프링 부트(Gradle/Maven, 최신 LTS 권장) 프로젝트 생성
- MariaDB 설치 및 user/database 세팅
- VS Code 또는 IntelliJ IDEA 등 개발툴 세팅, Git 버전관리 준비
- 필수 라이브러리(Thymeleaf, Spring Web, JPA, MariaDB Driver 등) 의존성 추가

---

## 2. DB 모델 및 ERD 설계
- 주요 테이블(Review, Member 등) 구조 설계 및 SQL/ERD 문서화 (Review 엔티티 필드 확정)
- DTO, Entity 등 자바 모델 설계 (BaseEntity, Review 엔티티, UploadResult 임베디드 타입, ReviewDTO, UploadResultDTO, PageRequestDTO, PageResponseDTO, SearchRequestDTO 생성)
- DB와 실제 연동 테스트 (ReviewRepository 테스트 완료)

---

## 3. 백엔드(Spring Boot) 기본 틀 구축
- 설정파일(application.properties)에서 DB 연결, 한글·타임존 등 환경 세팅
- Controller, Service, Repository 기본 구조 생성 (ReviewRepository, ReviewService, ReviewServiceImpl, ReviewController 기본 구조 생성)
- ModelMapper 빈 등록 (RootConfig 생성)
- QueryDSL 설정 및 Q-클래스 생성 환경 구축 (build.gradle 설정 완료)
- 간단한 REST 또는 MVC 기반 예시 엔드포인트로 정상 작동 확인 (컨트롤러 구현 중)

---

## 4. 현재까지 구현된 기능

### 리뷰 게시판 (Review Board)
- **전체 목록 조회 (페이징 포함):**
  - `GET /review/list`
  - QueryDSL을 이용한 동적 검색 및 페이징 처리 기능이 구현되었습니다.
- **게시글 등록:**
  - `GET /review/register` (등록 페이지)
  - `POST /review/register` (등록 처리)
- **게시글 상세 조회:**
  - `GET /review/read?review_id={id}`
- **게시글 수정:**
  - `GET /review/modify?review_id={id}` (수정 페이지)
  - `POST /review/modify` (수정 처리)
- **게시글 삭제:**
  - `POST /review/remove` (삭제 처리)

---

## 5. 앞으로 개발할 기능

### 리뷰 게시판 (Review Board)
- **사진 업로드 기능:**
  - 게시글 작성 및 수정 시 이미지 파일을 업로드하고, 서버에 저장하는 기능을 추가합니다.
  - 업로드된 이미지를 게시글 조회 시 함께 볼 수 있도록 구현합니다.
- **댓글 기능:**
  - 각 리뷰 게시글에 댓글을 작성, 수정, 삭제할 수 있는 기능을 추가합니다.
- **좋아요/추천 기능:**
  - 각 리뷰 게시글에 '좋아요' 또는 '추천'을 할 수 있는 기능을 추가합니다.

---

## 6. 현재 문제점 및 다음 작업
- **문제점:** `ReviewServiceTests`의 `testGetList()` 메서드가 `org.springframework.data.mapping.PropertyReferenceException` 오류로 실패했었으나, `ReviewSearchImpl`에서 정렬 로직을 수정하여 해결 시도.
- **다음 작업:**
  1. `gradlew clean test` 명령을 다시 실행하여 모든 테스트가 통과하는지 최종 확인.
  2. 테스트 통과 시, Thymeleaf 템플릿과 컨트롤러를 연결하여 화면 개발을 본격적으로 진행.
  3. 위 '앞으로 개발할 기능' 목록에 따라 사진 업로드 기능부터 구현 시작.