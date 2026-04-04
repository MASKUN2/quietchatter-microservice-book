package com.quietchatter.book.application.port.out

data class ExternalBook(
    val title: String,
    val isbn: String,
    val author: String,
    val thumbnailImage: String,
    val description: String,
    val externalLink: String
)
