package com.quietchatter.book.application.service

import com.quietchatter.book.application.`in`.Keyword
import com.quietchatter.book.application.port.out.BookRepository
import com.quietchatter.book.application.port.out.ExternalBook
import com.quietchatter.book.application.port.out.ExternalBookSearcher
import com.quietchatter.book.domain.Book
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import java.util.*

class BookQueryServiceTest {

    private val externalBookSearcher: ExternalBookSearcher = mock()
    private val bookRepository: BookRepository = mock()
    private val bookQueryService = BookQueryService(externalBookSearcher, bookRepository)

    @Test
    fun `키워드로 검색 시 외부 API 결과와 DB를 병합한다`() {
        // given
        val keyword = Keyword("test")
        val pageRequest = PageRequest.of(0, 10)
        
        val externalBook1 = ExternalBook("Title 1", "isbn1", "Author 1", "thumb1", "desc 1", "link 1")
        val externalBook2 = ExternalBook("Title 2", "isbn2", "Author 2", "thumb2", "desc 2", "link 2")
        val fetchedBooks = listOf(externalBook1, externalBook2)
        
        val existingBook1 = Book.newOf("Title 1", "isbn1")
        
        whenever(externalBookSearcher.findByKeyword(any(), any()))
            .thenReturn(SliceImpl(fetchedBooks, pageRequest, false))
            
        whenever(bookRepository.findByIsbnIn(any()))
            .thenReturn(listOf(existingBook1))
            
        whenever(bookRepository.save(any()))
            .thenAnswer { it.arguments[0] as Book }

        // when
        val result = bookQueryService.findBy(keyword, pageRequest)

        // then
        assertEquals(2, result.numberOfElements)
        assertEquals("Author 1", existingBook1.author)
        
        verify(externalBookSearcher).findByKeyword(any(), any())
        verify(bookRepository).findByIsbnIn(any())
        verify(bookRepository, times(1)).save(any())
    }
}
