# microservice-book 구현 스펙

## 1. 서비스 역할

책 정보와 관련된 모든 기능을 담당합니다.
외부 도서 검색 API(Kakao)를 통해 책을 검색하고, 결과를 DB에 캐싱하여 빠른 조회를 지원합니다.

## 2. 도메인 모델

### Book (책)

| 필드 | 타입 | 설명 |
|---|---|---|
| id | UUID | 기본 키 |
| title | String | 도서 제목 |
| isbn | String | ISBN (Unique) |
| author | String | 저자 |
| thumbnailImage | String | 썸네일 이미지 URL |
| description | String | 도서 설명 |
| externalLink | String | 외부 링크 (Kakao 도서 페이지) |
| createdAt | LocalDateTime | 생성일시 |
| updatedAt | LocalDateTime | 수정일시 |

## 3. API 명세

### GET /v1/books
키워드로 책을 검색합니다.

쿼리 파라미터:
* `keyword` (필수): 검색 키워드
* `page`: 페이지 번호 (기본값: 0)
* `size`: 페이지 크기 (기본값: 20)

처리 흐름:
1. Kakao 도서 검색 API를 호출하여 결과를 가져옵니다.
2. 결과의 ISBN을 기준으로 DB와 비교합니다.
3. DB에 없는 책은 저장하고, 있는 책은 최신 정보로 업데이트합니다.
4. 병합된 결과를 페이지네이션하여 반환합니다.

응답:
```json
{
  "content": [
    {
      "id": "uuid",
      "title": "책 제목",
      "isbn": "9788966262427",
      "author": "저자명",
      "thumbnailImage": "https://...",
      "description": "책 설명",
      "externalLink": "https://..."
    }
  ],
  "hasNext": true,
  "page": 0,
  "size": 20
}
```

### GET /v1/books/{bookId}
특정 ID의 책 상세 정보를 조회합니다.

응답:
```json
{
  "id": "uuid",
  "title": "책 제목",
  "isbn": "9788966262427",
  "author": "저자명",
  "thumbnailImage": "https://...",
  "description": "책 설명",
  "externalLink": "https://.."
}
```

## 4. 외부 API 연동: Kakao 도서 검색

### 연동 정보

* 엔드포인트: `https://dapi.kakao.com/v3/search/book`
* 인증: `Authorization: KakaoAK {REST_API_KEY}`
* 검색 파라미터: `query`, `page`, `size`, `target` (title/isbn/publisher/person)

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

## 5. 내부 API (서비스 간 통신용)

### GET /internal/books
ID 목록으로 책 정보를 일괄 조회합니다. (microservice-talk에서 사용)

쿼리 파라미터: `ids` (UUID 목록, 쉼표 구분)

응답: Book 목록 (배열)

## 6. 설정 구조

```yaml
server:
  port: 8082

app:
  kakao:
    rest-api-key: ${KAKAO_REST_API_KEY}  # Consul Config 또는 환경 변수에서 주입
```

## 7. 구현 우선순위

1. Book 도메인 및 JPA 설정
2. Kakao 도서 검색 API 클라이언트 구현
3. mergeOrPersist 서비스 로직 구현
4. 검색 및 상세 조회 API
5. 내부 API (책 일괄 조회)
