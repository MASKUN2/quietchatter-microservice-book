# AI Agent Guide - microservice-book

이 문서는 AI 에이전트가 microservice-book 프로젝트를 이해하고 개발을 돕기 위한 지침입니다.

## 1. 서비스 개요

* 역할: 책 정보 조회, 외부 도서 API 연동, DB 캐싱
* 담당 레거시 패키지: `book`
* 포트: 8082

## 2. 기술 스택

* 언어: Kotlin 1.9.x
* 프레임워크: Spring Boot 3.5.13
* 데이터베이스: PostgreSQL (JPA로 책 정보 캐싱)
* 외부 API: Kakao 도서 검색 API
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
    out/           BookJpaRepository.kt, KakaoBookSearchClient.kt
```

## 4. 에이전트 작업 지침

### A. 코드 포팅 규칙

* 레거시 Java 코드를 idiomatic Kotlin 코드로 변환하십시오.
* Data class를 적극 활용하십시오. Lombok은 사용하지 않습니다.
* 레거시의 `book/` 패키지 전체를 참고하여 포팅하십시오.
* `BookQueryService.java`의 mergeOrPersist 로직(외부 API 결과와 DB를 병합하는 패턴)은 핵심 비즈니스 로직이므로 정확히 포팅하십시오.
* 새로운 코드를 작성하거나 수정할 때마다 반드시 단위 테스트(Unit Test)를 함께 작성하고 통과를 확인하십시오.

### B. 외부 API 연동 규칙

* Kakao 도서 검색 API 호출은 `adaptor/out`에 구현하고, `ExternalBookSearcher` 포트 인터페이스를 통해 사용하십시오.
* API 키(API Key)는 코드에 하드코딩하지 말고 Consul Config 또는 환경 변수에서 주입받으십시오.
* 외부 API 호출 실패 시 빈 결과를 반환하고 서비스를 중단하지 마십시오.

### C. DB 캐싱 전략

* 외부 API 결과는 ISBN을 기준으로 DB에 저장하거나 업데이트합니다.
* 동일 ISBN의 책은 새로운 외부 API 결과로 제목, 저자, 썸네일, 설명, 링크를 업데이트합니다.
* DB에 이미 존재하면 저장 없이 업데이트만 수행합니다.

### D. 문서 규칙

* 마크다운 작성 시 굵게(bold)나 기울임(italics) 같은 강조 서식을 사용하지 않습니다.
* 마크다운 작성 시 이모티콘을 사용하지 않습니다.

## 5. 구현 스펙 참조

[docs/spec.md](./docs/spec.md)를 반드시 읽고 작업을 시작하십시오.
