package com.quietchatter.book.adaptor.`in`.web

import com.quietchatter.book.domain.Book
import java.util.*

data class BookResponse(
    val id: UUID,
    val title: String,
    val isbn: String,
    val author: String?,
    val thumbnailImageUrl: String?,
    val description: String?,
    val externalLinkUrl: String?
) {
    companion object {
        fun from(book: Book): BookResponse {
            return BookResponse(
                id = book.id!!,
                title = book.title,
                isbn = book.isbn,
                author = book.author,
                thumbnailImageUrl = book.thumbnailImage,
                description = book.description,
                externalLinkUrl = book.externalLink
            )
        }
    }
}
