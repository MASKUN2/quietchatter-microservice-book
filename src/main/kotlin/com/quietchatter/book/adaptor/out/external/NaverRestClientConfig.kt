package com.quietchatter.book.adaptor.out.external

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class NaverRestClientConfig {

    @Bean("naverRestClient")
    fun naverRestClient(
        builder: RestClient.Builder,
        @Value("\${naver.api.client-id}") clientId: String,
        @Value("\${naver.api.client-secret}") clientSecret: String
    ): RestClient {
        return builder.clone()
            .baseUrl("https://openapi.naver.com/v1/search/book.json")
            .defaultHeader("X-Naver-Client-Id", clientId)
            .defaultHeader("X-Naver-Client-Secret", clientSecret)
            .build()
    }
}
