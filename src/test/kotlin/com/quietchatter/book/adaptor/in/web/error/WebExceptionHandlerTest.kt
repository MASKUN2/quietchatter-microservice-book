package com.quietchatter.book.adaptor.`in`.web.error

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MissingRequestHeaderException

class WebExceptionHandlerTest {

    private val handler = WebExceptionHandler()

    private val dummyParameter = MethodParameter(
        WebExceptionHandlerTest::class.java.getDeclaredMethod("dummyMethod"), -1
    )

    @Suppress("unused")
    fun dummyMethod() {}

    @Test
    fun `missing X-Member-Id header returns 401`() {
        val ex = MissingRequestHeaderException("X-Member-Id", dummyParameter)
        val result = handler.handleMissingRequestHeader(ex)
        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.status)
    }

    @Test
    fun `missing other required header returns 400`() {
        val ex = MissingRequestHeaderException("X-Some-Header", dummyParameter)
        val result = handler.handleMissingRequestHeader(ex)
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.status)
    }
}
