package com.quietchatter.book.application.`in`

import com.quietchatter.book.domain.Book
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface BookQueryable {
    fun findBy(bookId: UUID): Book
    fun findBy(bookIds: List<UUID>): List<Book>
    fun findBy(keyword: Keyword, pageRequest: Pageable): Slice<Book>
}
