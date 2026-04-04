package com.quietchatter.book.adaptor.out.persistence

import com.quietchatter.book.domain.Book
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BookJpaRepository : JpaRepository<Book, UUID> {
    fun findByIsbnIn(isbns: Collection<String>): List<Book>
    fun findByIdIn(ids: Collection<UUID>): List<Book>
}
