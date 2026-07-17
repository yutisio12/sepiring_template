package com.sepring.template.dto

data class PaginationRequest(
    val page: Int = 0,
    val size: Int = 20,
    val sort: String? = null
)
