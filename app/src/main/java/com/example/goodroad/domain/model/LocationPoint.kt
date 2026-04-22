package com.example.goodroad.domain.model

data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long? = null
) {
    fun ToLatLanString() : String = "$latitude,$longitude"
}
