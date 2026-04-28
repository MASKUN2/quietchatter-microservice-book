# microservice-book 구현 스펙

## 1. 서비스 역할

책 정보와 관련된 모든 기능을 담당합니다.
외부 도서 검색 API(Naver)를 통해 책을 검색하고, 결과를 DB에 캐싱하여 빠른 조회를 지원합니다.

## 2. 도메인 모델

### Book (책)

| 필드 | 타입 | 설명 |
|---|---|---|
| id | UUID | 기본 키 |
| title | String | 도서 제목 |
| isbn | String | ISBN (Unique) |
| author | String? | 저자 (nullable) |
| thumbnailImageUrl | String? | 썸네일 이미지 URL (nullable) |
| description | String? | 도서 설명 (nullable) |
| externalLinkUrl | String? | 외부 링크 (Naver 도서 페이지, nullable) |
| createdAt | LocalDateTime | 생성일시 |
| updatedAt | LocalDateTime | 수정일시 |

## 3. API 명세

### GET /api/books (키워드 검색)
키워드로 책을 검색합니다. (네이버 API 연동 및 DB 캐싱)

쿼리 파라미터:
* `keyword` (필수): 검색 키워드
* `page`: 페이지 번호 (기본값: 0)
* `size`: 페이지 크기 (기본값: 20)
* `sort`: 정렬 기준

응답: `Slice<BookResponse>` 형식
```json
{
  "content": [
    {
      "id": "uuid",
      "title": "책 제목",
      "isbn": "9788966262427",
      "author": "저자명",
      "thumbnailImageUrl": "https://...",
      "description": "책 설명",
      "externalLinkUrl": "https://..."
    }
  ],
  "hasNext": true,
  "page": 0,
  "size": 20
}
```

### GET /api/books (ID 일괄 조회)
여러 개의 책 ID(UUID)를 받아 해당 도서 정보를 한꺼번에 조회합니다.

쿼리 파라미터:
* `id` (필수, 중복 가능): 조회할 책의 UUID 리스트 (예: `?id=uuid1&id=uuid2`)

응답: `List<BookResponse>` 형식

### GET /api/books/{bookId} (단일 상세 조회)
특정 ID의 책 상세 정보를 조회합니다.

응답: `BookResponse` 형식
```json
{
  "id": "uuid",
  "title": "책 제목",
  "isbn": "9788966262427",
  "author": "저자명",
  "thumbnailImageUrl": "https://...",
  "description": "책 설명",
  "externalLinkUrl": "https://.."
}
```

### GET /api/v1/spec (OpenAPI 스펙 조회)
서버에서 관리 중인 `openapi3.yaml` 스펙을 YAML 형식으로 반환합니다.

## 4. 외부 API 연동: Naver 도서 검색

### 연동 정보

* 엔드포인트: `https://openapi.naver.com/v1/search/book.json`
* 인증: 
    * `X-Naver-Client-Id: {CLIENT_ID}`
    * `X-Naver-Client-Secret: {CLIENT_SECRET}`
* 검색 파라미터: `query`, `start`, `display`

### ExternalBook 인터페이스 (포트)

```kotlin
interface ExternalBookSearcher {
    fun findByKeyword(keyword: Keyword, pageable: Pageable): Slice<ExternalBook>
}

data class ExternalBook(
    val title: String,
    val isbn: String,
    val author: String,
    val thumbnailImage: String,
    val description: String,
    val externalLink: String
)
```

### 캐싱 병합 로직 (중요)

레거시의 `BookQueryService.mergeOrPersist` 로직을 기준으로 구현합니다.

```
1. 외부 API 결과 목록에서 ISBN을 모두 수집
2. 해당 ISBN 목록으로 DB에서 기존 책 조회
3. (제목, ISBN) 쌍을 키로 Map 생성
4. 외부 API 각 결과에 대해:
   - Map에 존재: 기존 Book 엔티티의 정보(제목, 저자 등)를 업데이트하고 반환
   - Map에 없음: 새 Book 엔티티를 생성하여 저장하고 반환
5. 병합된 Slice<Book> 반환
```

## 5. 설정 구조

```yaml
server:
  port: 8080

naver:
  api:
    # Local/Dev 환경은 환경변수 주입, Prod 환경은 AWS Secrets Manager 사용
    client-id: ${NAVER_CLIENT_ID} 또는 ${quietchatter-naver-client-id}
    client-secret: ${NAVER_CLIENT_SECRET} 또는 ${quietchatter-naver-client-secret}
```

## 6. 구현 우선순위

1. Book 도메인 및 JPA 설정
2. Naver 도서 검색 API 클라이언트 구현
3. mergeOrPersist 서비스 로직 구현
4. 검색 및 상세 조회 API (일괄 조회 포함)
5. OpenAPI 스펙 서버 구현

## 7. 로깅 및 관찰 가능성

### 외부 API 응답 로깅
`NaverBookSearcher`에서는 외부 API 요청 후 수신된 원본 응답을 디버그 레벨로 로깅합니다. 장애 발생 시 외부 연동 상태를 확인하는 용도로 활용합니다.

활성화 방법 (`application-local.yml` 등):
```yaml
logging:
  level:
    com.quietchatter.book.adaptor.out.external: DEBUG
```
