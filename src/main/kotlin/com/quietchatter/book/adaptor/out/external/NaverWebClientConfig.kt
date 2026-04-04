package com.quietchatter.book.adaptor.out.external

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class NaverWebClientConfig {

    @Bean("naverWebClient")
    fun naverWebClient(
        builder: WebClient.Builder,
        @Value("\${naver.api.client-id}") clientId: String,
        @Value("\${naver.api.client-secret}") clientSecret: String
    ): WebClient {
        return builder.clone()
            .baseUrl("https://openapi.naver.com/v1/search/book.json")
            .defaultHeader("X-Naver-Client-Id", clientId)
            .defaultHeader("X-Naver-Client-Secret", clientSecret)
            .build()
    }
}
