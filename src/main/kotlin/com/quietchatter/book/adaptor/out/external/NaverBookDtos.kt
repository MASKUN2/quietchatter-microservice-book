package com.quietchatter.book.adaptor.out.external

import com.fasterxml.jackson.annotation.JsonProperty

data class NaverBookSearchResponse(
    @JsonProperty("lastBuildDate") val lastBuildDate: String,
    @JsonProperty("total") val total: Long,
    @JsonProperty("start") val start: Int,
    @JsonProperty("display") val display: Int,
    @JsonProperty("items") val items: List<NaverBookItem>
)

data class NaverBookItem(
    @JsonProperty("title") val title: String,
    @JsonProperty("link") val link: String,
    @JsonProperty("image") val image: String,
    @JsonProperty("author") val author: String,
    @JsonProperty("price") val price: String,
    @JsonProperty("discount") val discount: String,
    @JsonProperty("publisher") val publisher: String,
    @JsonProperty("isbn") val isbn: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("pubdate") val pubdate: String
)
