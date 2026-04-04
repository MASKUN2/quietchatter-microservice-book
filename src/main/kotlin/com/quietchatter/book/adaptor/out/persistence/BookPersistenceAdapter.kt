package com.quietchatter.book.adaptor.out.persistence

import com.quietchatter.book.application.port.out.BookRepository
import com.quietchatter.book.domain.Book
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class BookPersistenceAdapter(
    private val bookJpaRepository: BookJpaRepository
) : BookRepository {

    override fun findById(id: UUID): Optional<Book> = bookJpaRepository.findById(id)

    override fun findByIdIn(ids: Collection<UUID>): List<Book> = bookJpaRepository.findByIdIn(ids)

    override fun findByIsbnIn(isbns: Collection<String>): List<Book> = bookJpaRepository.findByIsbnIn(isbns)

    override fun save(book: Book): Book = bookJpaRepository.save(book)

    override fun saveAll(books: Iterable<Book>): List<Book> = bookJpaRepository.saveAll(books)
}
