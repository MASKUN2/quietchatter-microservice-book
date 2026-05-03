package com.quietchatter.book.adaptor.`in`.web

import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.quietchatter.book.application.`in`.BookQueryable
import com.quietchatter.book.application.`in`.Keyword
import com.quietchatter.book.domain.Book
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(controllers = [BookApi::class])
@AutoConfigureRestDocs
@Tag("restdocs")
class BookApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var bookQueryable: BookQueryable

    @Test
    fun `특정 ID로 책 상세 정보를 조회한다`() {
        // given
        val bookId = UUID.randomUUID()
        val book = Book.newOf("Test Book Title", "1234567890123")
        book.updateAuthor("Test Author")
        book.updateThumbnailImage("https://example.com/thumbnail.jpg")
        book.updateDescription("A test book description")
        book.updateExternalLink("https://shopping.naver.com/book/1234567890123")

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
            .andExpect(jsonPath("$.title").value("Test Book Title"))
            .andExpect(jsonPath("$.isbn").value("1234567890123"))
            .andDo(
                document(
                    "get-book-detail",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("Books")
                            .description("Get detailed information of a specific book")
                            .pathParameters(
                                parameterWithName("bookId").description("The unique identifier of the book")
                            )
                            .responseFields(
                                fieldWithPath("id").description("Book ID"),
                                fieldWithPath("title").description("Book Title"),
                                fieldWithPath("isbn").description("Book ISBN"),
                                fieldWithPath("author").description("Author (optional)").optional(),
                                fieldWithPath("thumbnailImageUrl").description("Thumbnail Image URL (optional)").optional(),
                                fieldWithPath("description").description("Description (optional)").optional(),
                                fieldWithPath("externalLinkUrl").description("External Link URL (optional)").optional()
                            )
                            .responseSchema(Schema.schema("BookResponse"))
                            .build()
                    )
                )
            )
    }

    @Test
    fun `키워드로 책을 검색한다`() {
        // given
        val keyword = "Test"
        val bookId = UUID.randomUUID()
        val book = Book.newOf("Test Book Title", "1234567890123")
        book.updateAuthor("Test Author")
        book.updateThumbnailImage("https://example.com/thumbnail.jpg")
        book.updateDescription("A test book description")
        book.updateExternalLink("https://shopping.naver.com/book/1234567890123")

        val idField = com.quietchatter.book.domain.BaseEntity::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(book, bookId)

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
            .andExpect(jsonPath("$.content[0].title").value("Test Book Title"))
            .andExpect(jsonPath("$.content[0].isbn").value("1234567890123"))
            .andDo(
                document(
                    "search-books",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("Books")
                            .description("Search books by keyword (title, author, or ISBN)")
                            .queryParameters(
                                parameterWithName("keyword").description("The search keyword"),
                                parameterWithName("page").description("Page number (starts from 0)").optional(),
                                parameterWithName("size").description("Page size").optional()
                            )
                            .responseFields(
                                fieldWithPath("content[].id").description("Book ID"),
                                fieldWithPath("content[].title").description("Book Title"),
                                fieldWithPath("content[].isbn").description("Book ISBN"),
                                fieldWithPath("content[].author").description("Author").optional(),
                                fieldWithPath("content[].thumbnailImageUrl").description("Thumbnail Image URL").optional(),
                                fieldWithPath("content[].description").description("Description").optional(),
                                fieldWithPath("content[].externalLinkUrl").description("External Link URL").optional(),
                                fieldWithPath("pageable").description("Pageable info"),
                                fieldWithPath("last").description("Is last page"),
                                fieldWithPath("first").description("Is first page"),
                                fieldWithPath("size").description("Page size"),
                                fieldWithPath("number").description("Current page number"),
                                fieldWithPath("sort.empty").description("Sort empty"),
                                fieldWithPath("sort.sorted").description("Sort sorted"),
                                fieldWithPath("sort.unsorted").description("Sort unsorted"),
                                fieldWithPath("numberOfElements").description("Number of elements in current page"),
                                fieldWithPath("empty").description("Is empty")
                            )
                            .responseSchema(Schema.schema("BookSliceResponse"))
                            .build()
                    )
                )
            )
    }

    @Test
    fun `ID 목록으로 책들을 조회한다`() {
        // given
        val bookId = UUID.randomUUID()
        val book = Book.newOf("Test Book Title", "1234567890123")
        book.updateAuthor("Test Author")
        book.updateThumbnailImage("https://example.com/thumbnail.jpg")
        book.updateDescription("A test book description")
        book.updateExternalLink("https://shopping.naver.com/book/1234567890123")

        val idField = com.quietchatter.book.domain.BaseEntity::class.java.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(book, bookId)

        whenever(bookQueryable.findBy(listOf(bookId))).thenReturn(listOf(book))

        // when & then
        mockMvc.perform(
            get("/api/books")
                .param("id", bookId.toString())
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(bookId.toString()))
            .andDo(
                document(
                    "get-books-by-ids",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("Books")
                            .description("Get a list of books by their IDs")
                            .queryParameters(
                                parameterWithName("id").description("Comma-separated list of book IDs")
                            )
                            .responseFields(
                                fieldWithPath("[].id").description("Book ID"),
                                fieldWithPath("[].title").description("Book Title"),
                                fieldWithPath("[].isbn").description("Book ISBN"),
                                fieldWithPath("[].author").description("Author (optional)").optional(),
                                fieldWithPath("[].thumbnailImageUrl").description("Thumbnail Image URL (optional)").optional(),
                                fieldWithPath("[].description").description("Description (optional)").optional(),
                                fieldWithPath("[].externalLinkUrl").description("External Link URL (optional)").optional()
                            )
                            .responseSchema(Schema.schema("BookListResponse"))
                            .build()
                    )
                )
            )
    }
}
