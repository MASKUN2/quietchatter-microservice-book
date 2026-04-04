package com.quietchatter.book.application.port.out

import com.quietchatter.book.application.`in`.Keyword
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface ExternalBookSearcher {
    fun findByKeyword(keyword: Keyword, pageable: Pageable): Slice<ExternalBook>
}
