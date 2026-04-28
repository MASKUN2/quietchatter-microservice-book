# AI Agent Guide - microservice-book

이 문서는 AI 에이전트가 microservice-book 프로젝트를 이해하고 개발을 돕기 위한 지침입니다.

## 1. 서비스 개요

* 역할: 책 정보 조회, 외부 도서 API 연동, DB 캐싱
* 담당 레거시 패키지: book
* 포트: 8080

## 2. 기술 스택

* 언어: Kotlin 1.9.x
* 프레임워크: Spring Boot 3.5.13
* 데이터베이스: PostgreSQL (JPA로 책 정보 캐싱)
* 외부 API: Naver 도서 검색 API
* 의존성: spring-boot-starter-web, data-jpa, consul-discovery, consul-config

## 3. 아키텍처

헥사고날 아키텍처(Ports and Adapters)를 사용합니다.

```
adaptor/in  (Web Controller: @RestController)
    |
application (Use Case Service: Port Interface + Impl)
    |
adaptor/out (JPA Repository, External Book API Client)
    |
domain      (Book Entity: 순수 비즈니스 로직)
```

패키지 구조 예시:
```
com.quietchatter.book/
  domain/          Book.kt
  application/
    in/            BookQueryable.kt, Keyword.kt
    out/           BookRepository.kt, ExternalBookSearcher.kt
  adaptor/
    in/            BookController.kt, BookResponse.kt
    out/           BookJpaRepository.kt, NaverBookSearchClient.kt
```

## 4. 에이전트 작업 지침

모든 작업 시작 전 및 작업 중에 superpowers 스킬 목록을 항상 확인하고 상황에 맞는 스킬을 활성화하여 사용하십시오.

### A. 공통 원칙

* 모든 서비스는 헥사고날 아키텍처를 따르며, 어댑터 패키지 명칭은 adaptor로 통일합니다.

### B. 레거시 참조 및 포팅 규칙

* 반드시 @legacy-quiet-chatter/**의 구현 방식, 패키지 구조, 테스트 패턴을 최우선으로 참고하십시오.
* 레거시의 Java 코드를 idiomatic Kotlin 코드로 변환하되, 핵심 비즈니스 로직(특히 BookQueryService의 mergeOrPersist)은 레거시의 동작 방식을 엄격히 따릅니다.
* Data class를 적극 활용하고 Lombok 사용을 금지합니다.
* 레거시의 book/ 패키지뿐만 아니라 persistence/BaseEntity.java, web/WebExceptionHandler.java 등 공통 관심사 구현 방식도 참고하십시오.

### C. 아키텍처 및 API 설계

* 레거시와 동일하게 헥사고날 아키텍처를 유지하며, 의존성 방향(Adapter -> Application -> Domain)을 준수하십시오.
* API 경로는 레거시의 최신 반영분인 /api/books 형식을 따르며, kebab-case URI와 camelCase JSON 필드명을 사용합니다.
* 에러 처리는 레거시의 WebExceptionHandler 및 ProblemDetail(RFC 7807) 방식을 참고하여 표준화하십시오.

### D. 데이터베이스 및 Flyway

* 데이터베이스 스키마 변경 시 레거시의 src/main/resources/db/migration 구조를 참고하여 Flyway 스크립트를 작성하십시오.
* 엔티티 정의 시 레거시의 Book.java 인덱스 설정(idx_book_isbn, idx_book_title)을 반드시 포함하십시오.

### E. 테스트 및 검증

* 레거시의 BookApiTest.java, BookQueryServiceTest.java 등에서 사용된 Test-Driven Documentation(RestDocs) 및 Mocking 패턴을 그대로 적용하십시오.
* 모든 기능 구현 시 단위 테스트와 API 문서화 테스트를 병행해야 합니다.
* 환경 설정 오류로 인한 테스트 실패 방지를 위해 레거시의 src/test/resources/application.yml 설정을 참고하십시오.

### F. 메시징 및 이벤트 처리 규칙

* 모든 외부 이벤트 발행은 트랜잭셔널 아웃박스(Transactional Outbox) 패턴을 따릅니다.
* 이벤트 직렬화 포맷은 평면화된 JSON(Flattened JSON)을 사용합니다.
* 메타데이터 필드(ID, 타입, 시간, 애그리거트 ID)에는 `evt_` 접두어를 사용하여 비즈니스 데이터와 구분합니다.
* 각 서비스는 소비하는 이벤트에 대해 독립적인 DTO를 정의하여 결합도를 낮춥니다.
* 발행되는 메시지의 페이로드는 서비스 내 정의된 Integration Event DTO 인스턴스를 사용하십시오.

### G. 문서 및 환경 설정

* Naver API 연동 구현 시 adaptor/out에 구현하고, ExternalBookSearcher 포트 인터페이스를 통해 사용하십시오.
* API Key 등 민감 정보는 레거시의 설정을 참고하여 외부 주입(Consul/Env) 방식으로 처리하십시오.
* 마크다운 작성 시 강조 서식(bold, italics)과 이모티콘 사용을 절대 금지합니다.

## 5. 구현 스펙 참조

[docs/spec.md](./docs/spec.md)를 반드시 읽고 작업을 시작하십시오.
