package com.quietchatter.book.application.`in`

@JvmInline
value class Keyword(val value: String) {
    init {
        require(value.isNotBlank()) { "Keyword cannot be blank" }
    }
}
