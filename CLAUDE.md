# CLAUDE.md - microservice-book

작업 전 README.md를 읽으십시오. 서비스 개요, 기술 스택, API 명세, 캐싱 병합 로직은 README.md에 있습니다.

루트 프로젝트의 CLAUDE.md에 정의된 공통 원칙도 확인하십시오.

## 작업 지침

### A. 레거시 참조 및 포팅 규칙

- 레거시의 book/ 패키지 구현 방식과 테스트 패턴을 최우선으로 참고하십시오.
- mergeOrPersist 핵심 비즈니스 로직은 레거시의 동작 방식을 엄격히 따릅니다.
- 레거시의 persistence/BaseEntity.java, web/WebExceptionHandler.java 등 공통 관심사 구현 방식도 참고하십시오.
- 파일 치환(replace) 시 퍼지 매칭(Fuzzy Match)으로 인한 의도치 않은 코드 삭제를 막기 위해, 항상 파일 원본을 먼저 읽고 정확한 대상 문자열을 지정하십시오.

### B. 데이터베이스 및 Flyway

- 스키마 변경 시 src/main/resources/db/migration에 Flyway 스크립트를 작성하십시오.
- 레거시의 Book.java 인덱스 설정(idx_book_isbn, idx_book_title)을 포함하십시오.

### C. 테스트 및 검증

- 레거시의 BookApiTest.java, BookQueryServiceTest.java의 RestDocs 및 Mocking 패턴을 적용하십시오.
- 모든 기능 구현 시 단위 테스트와 API 문서화 테스트를 병행하십시오.

### D. 메시징 규칙

- 모든 이벤트 발행은 Transactional Outbox 패턴을 따른다.
- 이벤트 포맷: CloudEvents 1.0. BookIntegrationEvent 클래스를 사용하며 필드는 specversion, id, source, type, time, subject, datacontenttype, data를 포함한다.
- type 필드 명명 규칙: com.quietchatter.book.{EventName} (예: com.quietchatter.book.BookCreatedEvent).
- time 필드: LocalDateTime.atOffset(ZoneOffset.UTC).toString()으로 RFC 3339 형식 직렬화.
- BookQueryService는 OutboxEventPersistable 포트를 통해 이벤트를 저장한다. OutboxEventRepository를 직접 주입하지 마십시오.