package com.quietchatter.book.adaptor.out.external

import com.quietchatter.book.application.`in`.Keyword
import com.quietchatter.book.application.port.out.ExternalBook
import com.quietchatter.book.application.port.out.ExternalBookSearcher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class NaverBookSearcher(
    @Qualifier("naverWebClient") private val naverClient: WebClient
) : ExternalBookSearcher {

    override fun findByKeyword(keyword: Keyword, pageable: Pageable): Slice<ExternalBook> {
        val pageNumber = pageable.pageNumber
        val pageSize = pageable.pageSize
        val start = (pageNumber * pageSize) + 1 // Naver API는 1부터 시작

        val response = naverClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .queryParam("query", keyword.value)
                    .queryParam("start", start)
                    .queryParam("display", pageSize)
                    .build()
            }
            .retrieve()
            .bodyToMono(NaverBookSearchResponse::class.java)
            .block() ?: throw RuntimeException("Failed to fetch from Naver API")

        val externalBooks = response.items
            .filter { it.isbn.isNotBlank() }
            .map { item ->
                ExternalBook(
                    title = item.title,
                    isbn = item.isbn,
                    author = item.author,
                    thumbnailImage = item.image,
                    description = item.description,
                    externalLink = item.link
                )
            }

        val hasNext = response.start + response.display <= response.total
        return SliceImpl(externalBooks, pageable, hasNext)
    }
}
