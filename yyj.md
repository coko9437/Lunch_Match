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
  - `GET /revie
  - `GET /view/{fileName}` (조회 처리, `FileController`)
  - `DELETE /removeFile/{fileName}` (삭제 처리, `FileController`)

---

## 5. 최근 진행한 작업 및 변경 사항

- **파일 업로드 인덱싱 오류 해결:**
  - `register.js`에서 파일 업로드 시 인덱스를 소수점으로 계산하던 버그를 `Math.floor()`를 사용하여 정수로 수정하여 `InvalidPropertyException` 오류를 해결했습니다.
- **리뷰 목록 썸네일 표시 기능 구현:**
  - `list.html`에 썸네일 이미지와 업로드된 이미지 개수를 표시하는 UI를 추가했습니다.
  - `UploadResultDTO`에 썸네일 링크를 반환하는 `getThumbnailLink()` 메서드를 추가했습니다.
- **파일 조회 컨트롤러 분리 및 URL 수정:**
  - 파일 조회를 담당하는 `FileController`를 별도로 생성했습니다.
  - `list.html`에서 썸네일 이미지 요청 URL을 `/view/{fileName}`으로 수정하여 `FileController`와 연동했습니다.

---

## 6. 현재 문제점 및 다음 작업

- **문제점:** 리뷰 목록 페이지에서 썸네일 이미지가 표시되지 않는 문제가 있습니다.
  - **원인 추정:** `FileController`의 URL 경로 문제 또는 MinIO 파일 접근 관련 문제로 예상됩니다.
- **다음 작업:**
  1.  **썸네일 표시 문제 해결:**
      - 브라우저 개발자 도구(F12)의 네트워크 탭에서 이미지 요청 URL이 올바른지, 서버로부터 404 오류 등이 발생하는지 확인합니다.
      - `FileController`의 `@GetMapping("/view/{fileName}")`이 정상적으로 호출되는지 로그를 통해 확인합니다.
      - `UploadUtil`의 `getFileFromMinio` 메서드가 MinIO에서 파일을 정상적으로 가져오는지 디버깅합니다.
  2.  **기능 안정화:** 썸네일 문제가 해결되면, 파일 업로드, 조회, 삭제 기능 전반에 대한 테스트를 진행하여 안정성을 확보합니다.
  3.  **다음 기능 개발:** 댓글 기능 또는 좋아요/추천 기능 개발을 시작합니다.
