# microservice-book

QuietChatter의 도서 도메인 서비스. 네이버 도서 검색 API를 통해 책을 조회하고 결과를 DB에 캐싱하여 제공한다.

## 기술 스택

- 언어: Kotlin 1.9.25
- 프레임워크: Spring Boot 3.5.13
- 런타임: JDK 21 Virtual Threads 활성화
- 데이터베이스: PostgreSQL (JPA, Flyway)
- 메시징: Spring Cloud Stream + Kafka (Redpanda)
- 외부 API: Naver 도서 검색 API
- 포트: 8081 (k8s 배포 시 SERVER_PORT 환경변수로 주입, 로컬 기본값 8080)

## 패키지 구조

헥사고날 아키텍처.

```
com.quietchatter.book/
  domain/          Book.kt
  application/
    in/            BookQueryable.kt, Keyword.kt
    out/           BookRepository.kt, ExternalBookSearcher.kt
  adaptor/
    in/web/        BookApi.kt, SpecController.kt
    out/           BookJpaRepository.kt, NaverBookSearchClient.kt
```

## API 명세

| 메서드 | 경로 | 설명 |
|---|---|---|
| GET | /api/books?keyword= | 키워드로 도서 검색 (Naver API + DB 캐싱). 응답: Slice<BookResponse> |
| GET | /api/books?id=&id= | 복수 ID 일괄 조회. 응답: List<BookResponse> |
| GET | /api/books/{bookId} | 단일 도서 상세 조회 |
| GET | /api/v1/spec | OpenAPI 스펙 YAML 반환 |

## 도메인 모델

Book: id(UUID), title, isbn(unique), author?, thumbnailImageUrl?, description?, externalLinkUrl?

## 캐싱 병합 로직 (mergeOrPersist)

레거시의 BookQueryService.mergeOrPersist 로직을 기준으로 구현한다.

1. 외부 API 결과에서 ISBN 목록 수집
2. 해당 ISBN으로 DB에서 기존 Book 조회
3. (제목, ISBN) 쌍을 키로 Map 생성
4. 외부 API 각 결과: Map에 존재하면 업데이트, 없으면 신규 저장
5. 병합된 Slice<Book> 반환

## 환경변수

| 변수 | 용도 |
|---|---|
| NAVER_CLIENT_ID | Naver 도서 검색 API Client ID |
| NAVER_CLIENT_SECRET | Naver 도서 검색 API Client Secret |
| DB_URL, DB_USERNAME, DB_PASSWORD | PostgreSQL 접속 정보 |
| KAFKA_BROKERS | Redpanda 브로커 주소 |

## 로컬 실행

사전 요구 사항: Docker, JDK 21

```bash
./gradlew bootRun
```
