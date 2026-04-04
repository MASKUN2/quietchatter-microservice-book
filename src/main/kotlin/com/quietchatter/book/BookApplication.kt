package com.quietchatter.book

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class BookApplication

fun main(args: Array<String>) {
    runApplication<BookApplication>(*args)
}
