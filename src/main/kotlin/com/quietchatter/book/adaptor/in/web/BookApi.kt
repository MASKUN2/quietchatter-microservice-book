package com.quietchatter.book.adaptor.`in`.web

import com.quietchatter.book.application.`in`.BookQueryable
import com.quietchatter.book.application.`in`.Keyword
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/books")
class BookApi(
    private val bookQueryable: BookQueryable
) {

    @GetMapping(params = ["keyword"])
    fun search(
        @PageableDefault pageable: Pageable,
        @RequestParam(name = "keyword") keywordValue: String
    ): ResponseEntity<Slice<BookResponse>> {
        val keyword = Keyword(keywordValue)
        val slice = bookQueryable.findBy(keyword, pageable)
            .map { BookResponse.from(it) }
        return ResponseEntity.ok(slice)
    }

    @GetMapping(params = ["id"])
    fun getByIds(@RequestParam(name = "id") ids: List<UUID>): ResponseEntity<List<BookResponse>> {
        val books = bookQueryable.findBy(ids)
        val responses = books.map { BookResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    @GetMapping("/internal/books")
    fun getInternalByIds(@RequestParam(name = "ids") ids: List<UUID>): ResponseEntity<List<BookResponse>> {
        val books = bookQueryable.findBy(ids)
        val responses = books.map { BookResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    @GetMapping("/{bookId}")
    fun getDetail(@PathVariable(name = "bookId") bookId: UUID): ResponseEntity<BookResponse> {
        val book = bookQueryable.findBy(bookId)
        return ResponseEntity.ok(BookResponse.from(book))
    }
}
