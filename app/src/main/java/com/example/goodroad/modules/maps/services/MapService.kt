package com.example.goodroad.modules.maps.services

import com.example.goodroad.data.network.route.ObstacleResponse
import com.example.goodroad.data.network.route.PathResponse
import com.example.goodroad.data.network.utils.decodePoints
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource

class MapService {

    fun drawRouteWithSegments(
        map: MapLibreMap,
        allPoints: List<LatLng>,
        obstacles: List<ObstacleResponse>,
        routeType: String
    ) {
        map.getStyle { style ->
            val layerPrefix = "segmented-layer-$routeType"
            val sourcePrefix = "segmented-source-$routeType"

            for (i in 0 until 100) {
                style.removeLayer("$layerPrefix-$i")
                style.removeSource("$sourcePrefix-$i")
            }

            val defaultColor = when (routeType) {
                "fast" -> "#244975"      // Синий
                "balanced" -> "#8B7AC6"  // Фиолетовый
                "safe" -> "#6FAE8A"      // Зеленый
                else -> "#887058"        // Коричневый (дефолт)
            }

            for (i in 0 until allPoints.size - 1) {
                val segment = listOf(allPoints[i], allPoints[i + 1])
                val segmentCoordinates = segment.joinToString(", ") {
                    "[${it.longitude}, ${it.latitude}]"
                }

                val segmentColor = getSegmentColor(segment, obstacles, routeType, defaultColor)

                val segmentGeojson = """
                    {
                        "type": "Feature",
                        "geometry": {
                            "type": "LineString",
                            "coordinates": [$segmentCoordinates]
                        }
                    }
                """.trimIndent()

                val segmentSourceId = "$sourcePrefix-$i"
                val segmentLayerId = "$layerPrefix-$i"

                val source = GeoJsonSource(segmentSourceId, segmentGeojson)
                style.addSource(source)

                val lineLayer = LineLayer(segmentLayerId, segmentSourceId).apply {
                    setProperties(
                        PropertyFactory.lineColor(segmentColor),
                        PropertyFactory.lineWidth(6f),
                        PropertyFactory.lineOpacity(0.9f)
                    )
                }
                style.addLayer(lineLayer)
            }
        }
    }

    private fun getSegmentColor(
        segment: List<LatLng>,
        obstacles: List<ObstacleResponse>,
        routeType: String,
        defaultColor: String
    ): String {
        val segmentCenterLat = (segment[0].latitude + segment[1].latitude) / 2
        val segmentCenterLon = (segment[0].longitude + segment[1].longitude) / 2

        val nearbyObstacle = obstacles.firstOrNull { obstacle ->
            val distance = haversineDistance(
                segmentCenterLat, segmentCenterLon,
                obstacle.latitude, obstacle.longitude
            )
            distance < 10.0 // Радиус поиска 50 метров
        }

        return when (routeType) {
            "fast" -> {
                when (nearbyObstacle?.severity) {
                    1.toShort() -> "#FFC107"     // LITE — жёлтый
                    2.toShort() -> "#FF9800"     // MEDIUM — оранжевый
                    3.toShort() -> "#F44336"     // IMPOSSIBLE — красный
                    else -> defaultColor
                }
            }
            "balanced" -> {
                when (nearbyObstacle?.severity) {
                    1.toShort() -> "#FFC107"     // LITE — жёлтый
                    2.toShort() -> "#FF9800"     // MEDIUM — оранжевый
                    else -> defaultColor
                }
            }
            else -> defaultColor
        }
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // радиус Земли в метрах
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }

    fun addMarker(
        map: MapLibreMap,
        point: LatLng,
        markerId: String,
        color: String,
        radius: Float = 12f
    ) {
        map.getStyle { style ->
            val layerId = "$markerId-layer"
            val sourceId = "$markerId-source"

            style.removeLayer(layerId)
            style.removeSource(sourceId)

            val geojson = """
                {
                    "type": "FeatureCollection",
                    "features": [{
                        "type": "Feature",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [${point.longitude}, ${point.latitude}]
                        }
                    }]
                }
            """.trimIndent()

            val source = GeoJsonSource(sourceId, geojson)
            style.addSource(source)

            val circleLayer = CircleLayer(layerId, sourceId).apply {
                setProperties(
                    PropertyFactory.circleColor(color),
                    PropertyFactory.circleRadius(radius),
                    PropertyFactory.circleOpacity(0.8f),
                    PropertyFactory.circleStrokeColor("#FFFFFF"),
                    PropertyFactory.circleStrokeWidth(2f)
                )
            }
            style.addLayer(circleLayer)
        }
    }

    fun clearRouteLayers(map: MapLibreMap) {
        map.getStyle { style ->
            // Удаляем все слои маршрутов
            val routeTypes = listOf("fast", "balanced", "safe")
            routeTypes.forEach { routeType ->
                val layerPrefix = "segmented-layer-$routeType"
                val sourcePrefix = "segmented-source-$routeType"
                for (i in 0 until 100) {
                    style.removeLayer("$layerPrefix-$i")
                    style.removeSource("$sourcePrefix-$i")
                }
            }
        }
    }
}