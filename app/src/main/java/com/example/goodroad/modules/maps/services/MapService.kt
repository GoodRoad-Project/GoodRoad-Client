package com.example.goodroad.modules.maps.services

import android.util.Log
import com.example.goodroad.data.network.route.ObstacleResponse
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.expressions.Expression

class MapService {

    private data class RouteData(
        val points: List<LatLng>,
        val obstacles: List<ObstacleResponse>,
        val routeType: String
    )

    private val routes = mutableMapOf<String, RouteData>()

    private var currentDetailLevel = -1

    private val detailTolerances = listOf(
        100.0,
        60.0,
        30.0,
        15.0,
        5.0,
        0.0
    )

    fun setRoute(
        map: MapLibreMap,
        points: List<LatLng>,
        obstacles: List<ObstacleResponse>,
        routeType: String
    ) {
        if (points.size < 2) {
            Log.w(
                "RouteSimplification",
                "route=$routeType: недостаточно точек (${points.size})"
            )
            return
        }

        routes[routeType] = RouteData(
            points = points,
            obstacles = obstacles,
            routeType = routeType
        )

        Log.d(
            "RouteSimplification",
            "route=$routeType: маршрут сохранён, " +
                    "originalPoints=${points.size}, obstacles=${obstacles.size}"
        )

        drawRoutes(map)
    }

    private fun drawRoutes(map: MapLibreMap) {
        map.getStyle { style ->

            routes.forEach { (routeType, route) ->

                val detailLevel = currentDetailLevel.coerceAtLeast(0)
                val tolerance = detailTolerances[detailLevel]

                val simplifiedPoints = simplifyRoute(
                    points = route.points,
                    obstacles = route.obstacles,
                    toleranceMeters = tolerance
                )

                Log.d(
                    "RouteSimplification",
                    "route=$routeType, " +
                            "detailLevel=$detailLevel, " +
                            "tolerance=${tolerance}m, " +
                            "original=${route.points.size}, " +
                            "simplified=${simplifiedPoints.size}"
                )

                drawRoute(
                    style = style,
                    route = route,
                    points = simplifiedPoints
                )
            }
        }
    }

    private fun drawRoute(
        style: org.maplibre.android.maps.Style,
        route: RouteData,
        points: List<LatLng>
    ) {
        val routeType = route.routeType

        val sourceId = "route-source-$routeType"
        val layerId = "route-layer-$routeType"

        style.removeLayer(layerId)
        style.removeSource(sourceId)

        val features = points.zipWithNext().map { (start, end) ->

            val color = getSegmentColor(
                segment = listOf(start, end),
                obstacles = route.obstacles,
                routeType = routeType,
                defaultColor = getDefaultColor(routeType)
            )

            """
            {
                "type": "Feature",
                "properties": {
                    "color": "$color"
                },
                "geometry": {
                    "type": "LineString",
                    "coordinates": [
                        [${start.longitude}, ${start.latitude}],
                        [${end.longitude}, ${end.latitude}]
                    ]
                }
            }
            """.trimIndent()
        }

        val geoJson = """
            {
                "type": "FeatureCollection",
                "features": [
                    ${features.joinToString(",")}
                ]
            }
        """.trimIndent()

        val source = GeoJsonSource(sourceId, geoJson)
        style.addSource(source)

        val lineLayer = LineLayer(layerId, sourceId).apply {
            setProperties(
                PropertyFactory.lineColor(
                    Expression.get("color")
                ),
                PropertyFactory.lineWidth(6f),
                PropertyFactory.lineOpacity(0.9f),
                PropertyFactory.lineJoin("round"),
                PropertyFactory.lineCap("round")
            )
        }

        style.addLayer(lineLayer)
    }

    fun updateDetailLevel(
        map: MapLibreMap,
        zoom: Double
    ) {
        val newDetailLevel = when {
            zoom < 10.0 -> 0
            zoom < 11.0 -> 1
            zoom < 12.0 -> 2
            zoom < 13.0 -> 3
            zoom < 14.0 -> 4
            else -> 5
        }

        if (newDetailLevel == currentDetailLevel) {
            return
        }

        Log.d(
            "RouteSimplification",
            "zoom=$zoom, " +
                    "detailLevel=$newDetailLevel, " +
                    "tolerance=${detailTolerances[newDetailLevel]}m"
        )

        currentDetailLevel = newDetailLevel

        if (routes.isNotEmpty()) {
            drawRoutes(map)
        }
    }

    private fun simplifyRoute(
        points: List<LatLng>,
        obstacles: List<ObstacleResponse>,
        toleranceMeters: Double
    ): List<LatLng> {

        if (points.size <= 2 || toleranceMeters <= 0.0) {
            return points
        }

        val importantPoints = mutableSetOf<Int>()

        points.forEachIndexed { index, point ->

            val nearObstacle = obstacles.any { obstacle ->
                haversineDistance(
                    point.latitude,
                    point.longitude,
                    obstacle.latitude,
                    obstacle.longitude
                ) <= 3.0
            }

            if (nearObstacle) {
                importantPoints.add(index)

                if (index > 0) {
                    importantPoints.add(index - 1)
                }

                if (index < points.lastIndex) {
                    importantPoints.add(index + 1)
                }
            }
        }

        val result = douglasPeucker(
            points = points,
            toleranceMeters = toleranceMeters
        ).toMutableList()

        importantPoints.forEach { index ->
            val point = points[index]

            if (!result.contains(point)) {
                result.add(point)
            }
        }

        return restoreOriginalOrder(
            original = points,
            selected = result
        )
    }

    private fun douglasPeucker(
        points: List<LatLng>,
        toleranceMeters: Double
    ): List<LatLng> {

        if (points.size <= 2) {
            return points
        }

        val first = points.first()
        val last = points.last()

        var maxDistance = 0.0
        var maxIndex = 0

        for (i in 1 until points.lastIndex) {

            val distance = distanceToSegment(
                point = points[i],
                start = first,
                end = last
            )

            if (distance > maxDistance) {
                maxDistance = distance
                maxIndex = i
            }
        }

        if (maxDistance > toleranceMeters) {

            val left = douglasPeucker(
                points.subList(0, maxIndex + 1),
                toleranceMeters
            )

            val right = douglasPeucker(
                points.subList(maxIndex, points.size),
                toleranceMeters
            )

            return left.dropLast(1) + right
        }

        return listOf(first, last)
    }

    private fun restoreOriginalOrder(
        original: List<LatLng>,
        selected: List<LatLng>
    ): List<LatLng> {

        val selectedSet = selected.toSet()

        return original.filter {
            selectedSet.contains(it)
        }
    }

    private fun getSegmentColor(
        segment: List<LatLng>,
        obstacles: List<ObstacleResponse>,
        routeType: String,
        defaultColor: String
    ): String {

        val start = segment[0]
        val end = segment[1]

        val nearbyObstacle = obstacles.firstOrNull { obstacle ->

            val distance = distanceToSegment(
                point = LatLng(
                    obstacle.latitude,
                    obstacle.longitude
                ),
                start = start,
                end = end
            )

            distance <= 10.0
        }

        Log.d(
            "RouteObstacles",
            "route=$routeType, obstacles=${obstacles.size}, " +
                    "segmentStart=${start.latitude},${start.longitude}, " +
                    "segmentEnd=${end.latitude},${end.longitude}"
        )

        obstacles.forEach { obstacle ->
            val distance = distanceToSegment(
                point = LatLng(
                    obstacle.latitude,
                    obstacle.longitude
                ),
                start = start,
                end = end
            )

            Log.d(
                "RouteObstacles",
                "obstacle=${obstacle.id}, " +
                        "type=${obstacle.type}, " +
                        "severity=${obstacle.severity}, " +
                        "distance=${distance}m"
            )
        }

        return when (routeType) {

            "fast" -> {
                when (nearbyObstacle?.severity) {
                    1.toShort() -> "#FFC107"
                    2.toShort() -> "#FF9800"
                    3.toShort() -> "#F44336"
                    else -> defaultColor
                }
            }

            "balanced" -> {
                when (nearbyObstacle?.severity) {
                    1.toShort() -> "#FFC107"
                    2.toShort() -> "#FF9800"
                    else -> defaultColor
                }
            }

            else -> defaultColor
        }
    }

    private fun getDefaultColor(routeType: String): String {
        return when (routeType) {
            "fast" -> "#244975"
            "balanced" -> "#8B7AC6"
            "safe" -> "#6FAE8A"
            else -> "#887058"
        }
    }

    private fun distanceToSegment(
        point: LatLng,
        start: LatLng,
        end: LatLng
    ): Double {

        val lat = Math.toRadians(point.latitude)

        val metersPerLat = 111320.0
        val metersPerLon = 111320.0 * Math.cos(lat)

        val px = point.longitude * metersPerLon
        val py = point.latitude * metersPerLat

        val sx = start.longitude * metersPerLon
        val sy = start.latitude * metersPerLat

        val ex = end.longitude * metersPerLon
        val ey = end.latitude * metersPerLat

        val dx = ex - sx
        val dy = ey - sy

        if (dx == 0.0 && dy == 0.0) {
            return Math.sqrt((px - sx) * (px - sx) + (py - sy) * (py - sy))
        }

        val t = ((px - sx) * dx + (py - sy) * dy) / (dx * dx + dy * dy)

        val clampedT = t.coerceIn(0.0, 1.0)

        val closestX = sx + clampedT * dx
        val closestY = sy + clampedT * dy

        return Math.sqrt((px - closestX) * (px - closestX) + (py - closestY) * (py - closestY))
    }

    private fun haversineDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {

        val radius = 6371000.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) *
                    Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLon / 2) *
                    Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return radius * c
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
                            "coordinates": [
                                ${point.longitude},
                                ${point.latitude}
                            ]
                        }
                    }]
                }
            """.trimIndent()

            val source = GeoJsonSource(
                sourceId,
                geojson
            )

            style.addSource(source)

            val circleLayer = CircleLayer(
                layerId,
                sourceId
            ).apply {
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

            routes.keys.forEach { routeType ->

                style.removeLayer(
                    "route-layer-$routeType"
                )

                style.removeSource(
                    "route-source-$routeType"
                )
            }

            routes.clear()

            Log.d(
                "RouteSimplification",
                "Все маршруты очищены"
            )
        }
    }
}