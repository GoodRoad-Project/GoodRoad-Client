package com.example.goodroad.domain.model

import java.util.*

data class PlaceInfoResponse(
    val placeName: String,
    val address: String,
    val averageSeverity: Double? = null,
    val reviews: List<ReviewResp>? = null
)