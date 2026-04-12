package com.quietchatter.book.adaptor.out.external

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class NaverBookSearchResponse(
    @JsonProperty("lastBuildDate") val lastBuildDate: String? = null,
    @JsonProperty("total") val total: Long = 0,
    @JsonProperty("start") val start: Int = 1,
    @JsonProperty("display") val display: Int = 10,
    @JsonProperty("items") val items: List<NaverBookItem> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NaverBookItem(
    @JsonProperty("title") val title: String = "",
    @JsonProperty("link") val link: String? = null,
    @JsonProperty("image") val image: String? = null,
    @JsonProperty("author") val author: String = "",
    @JsonProperty("price") val price: String? = null,
    @JsonProperty("discount") val discount: String? = null,
    @JsonProperty("publisher") val publisher: String? = null,
    @JsonProperty("isbn") val isbn: String = "",
    @JsonProperty("description") val description: String = "",
    @JsonProperty("pubdate") val pubdate: String? = null
)
