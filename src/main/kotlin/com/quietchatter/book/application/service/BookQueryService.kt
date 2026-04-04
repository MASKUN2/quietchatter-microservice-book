package com.quietchatter.book.application.service

import com.quietchatter.book.application.`in`.BookQueryable
import com.quietchatter.book.application.`in`.Keyword
import com.quietchatter.book.application.port.out.BookRepository
import com.quietchatter.book.application.port.out.ExternalBook
import com.quietchatter.book.application.port.out.ExternalBookSearcher
import com.quietchatter.book.domain.Book
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class BookQueryService(
    private val externalBookSearcher: ExternalBookSearcher,
    private val bookRepository: BookRepository
) : BookQueryable {

    @Transactional(readOnly = true)
    override fun findBy(bookId: UUID): Book {
        return bookRepository.require(bookId)
    }

    @Transactional(readOnly = true)
    override fun findBy(bookIds: List<UUID>): List<Book> {
        return bookRepository.findByIdIn(bookIds)
    }

    @Transactional
    override fun findBy(keyword: Keyword, pageRequest: Pageable): Slice<Book> {
        val fetchedBooks = externalBookSearcher.findByKeyword(keyword, pageRequest)
        return mergeOrPersist(fetchedBooks)
    }

    private fun mergeOrPersist(fetchedBooks: Slice<ExternalBook>): Slice<Book> {
        val isbns = fetchedBooks.map { it.isbn }.toSet()
        val existsMap = mapExistsBy(isbns)

        return fetchedBooks.map { externalBook ->
            val key = TitleAndIsbn(externalBook.title, externalBook.isbn)
            val existingBook = existsMap[key]

            if (existingBook != null) {
                updateExistingBook(existingBook, externalBook)
            } else {
                saveNewBook(externalBook)
            }
        }
    }

    private fun mapExistsBy(isbns: Set<String>): Map<TitleAndIsbn, Book> {
        val existingBooks = bookRepository.findByIsbnIn(isbns)
        return existingBooks.associateBy { TitleAndIsbn(it.title, it.isbn) }
    }

    private fun updateExistingBook(book: Book, externalBook: ExternalBook): Book {
        book.update(externalBook.title)
        book.updateAuthor(externalBook.author)
        book.updateThumbnailImage(externalBook.thumbnailImage)
        book.updateDescription(externalBook.description)
        book.updateExternalLink(externalBook.externalLink)
        return book
    }

    private fun saveNewBook(externalBook: ExternalBook): Book {
        val book = Book.newOf(externalBook.title, externalBook.isbn)
        book.updateAuthor(externalBook.author)
        book.updateThumbnailImage(externalBook.thumbnailImage)
        book.updateDescription(externalBook.description)
        book.updateExternalLink(externalBook.externalLink)
        return bookRepository.save(book)
    }

    private data class TitleAndIsbn(val title: String, val isbn: String)
}
