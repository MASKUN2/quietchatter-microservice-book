package com.quietchatter.book.adaptor.`in`.web

import com.quietchatter.book.application.`in`.BookQueryable
import com.quietchatter.book.application.`in`.Keyword
import com.quietchatter.book.domain.Book
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(controllers = [BookApi::class])
class BookApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var bookQueryable: BookQueryable

    @Test
    fun `특정 ID로 책 상세 정보를 조회한다`() {
        // given
        val bookId = UUID.randomUUID()
        val book = Book.newOf("Test Book", "1234567890")
        
        val idField = com.quietchatter.book.domain.BaseEntity::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(book, bookId)
        
        whenever(bookQueryable.findBy(eq(bookId))).thenReturn(book)

        // when & then
        mockMvc.perform(
            get("/api/books/{bookId}", bookId)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(bookId.toString()))
            .andExpect(jsonPath("$.title").value("Test Book"))
            .andExpect(jsonPath("$.isbn").value("1234567890"))
    }

    @Test
    fun `키워드로 책을 검색한다`() {
        // given
        val keyword = "Test"
        val book = Book.newOf("Test Book", "1234567890")
        
        val idField = com.quietchatter.book.domain.BaseEntity::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(book, UUID.randomUUID())
        
        val slice = SliceImpl(listOf(book))

        whenever(bookQueryable.findBy(any<Keyword>(), any<Pageable>())).thenReturn(slice)

        // when & then
        mockMvc.perform(
            get("/api/books")
                .param("keyword", keyword)
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].title").value("Test Book"))
            .andExpect(jsonPath("$.content[0].isbn").value("1234567890"))
    }
}
