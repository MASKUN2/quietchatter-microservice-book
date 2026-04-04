package com.quietchatter.book.application.port.out

import com.quietchatter.book.domain.Book
import java.util.*

interface BookRepository {
    fun findById(id: UUID): Optional<Book>
    fun findByIdIn(ids: Collection<UUID>): List<Book>
    fun findByIsbnIn(isbns: Collection<String>): List<Book>
    fun save(book: Book): Book
    fun saveAll(books: Iterable<Book>): List<Book>

    fun require(id: UUID): Book {
        return findById(id).orElseThrow {
            NoSuchElementException("Book not found with id: $id")
        }
    }
}
