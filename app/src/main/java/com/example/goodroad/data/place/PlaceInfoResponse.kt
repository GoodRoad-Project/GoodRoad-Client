package com.example.goodroad.data.place

import com.example.goodroad.data.obstacle.model.ReviewResp

data class PlaceInfoResponse(
    var placeName: String? = null,
    var address: String? = null,
    var averageSeverity: Double? = null,
    var reviews: List<ReviewResp>? = null,
    var latitude: Double? = null,
    var longitude: Double? = null
)