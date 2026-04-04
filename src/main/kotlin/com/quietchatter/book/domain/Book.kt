package com.quietchatter.book.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "book",
    indexes = [
        Index(columnList = "isbn", name = "idx_book_isbn"),
        Index(columnList = "title", name = "idx_book_title")
    ]
)
class Book protected constructor(
    @Column(name = "title")
    var title: String,

    @Column(name = "isbn", nullable = false)
    val isbn: String
) : BaseEntity() {

    init {
        require(isbn.isNotBlank()) { "ISBN is required" }
    }

    @Column(name = "author")
    var author: String? = null
        protected set

    @Column(name = "thumbnail_image_url", columnDefinition = "TEXT")
    var thumbnailImage: String? = null
        protected set

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null
        protected set

    @Column(name = "external_link_url", columnDefinition = "TEXT")
    var externalLink: String? = null
        protected set

    fun update(newTitle: String) {
        this.title = newTitle
    }

    fun updateAuthor(author: String?) {
        this.author = author
    }

    fun updateThumbnailImage(thumbnailImage: String?) {
        this.thumbnailImage = thumbnailImage
    }

    fun updateDescription(description: String?) {
        this.description = description
    }

    fun updateExternalLink(externalLink: String?) {
        this.externalLink = externalLink
    }

    companion object {
        fun newOf(title: String, isbn: String): Book {
            return Book(title, isbn)
        }
    }
}
