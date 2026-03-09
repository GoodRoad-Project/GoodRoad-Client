package com.example.goodroad.features.network.utils

import com.google.maps.android.PolyUtil
import com.example.goodroad.domain.model.LocationPoint

fun decodePoints(encodedPoints: String): List<LocationPoint> {
    return PolyUtil.decode(encodedPoints).map {
        LocationPoint(
            latitude = it.latitude,
            longitude = it.longitude
        )
    }
}