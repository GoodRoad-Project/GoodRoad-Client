package com.example.goodroad.data.network

import com.example.goodroad.data.network.GoodRoadApi
import com.example.goodroad.data.network.route.RouteRequest
import com.example.goodroad.data.network.route.RouteObstaclePolicy
import com.example.goodroad.data.network.route.RouteResponse
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class GraphHopperApiTest {

    private lateinit var api: GoodRoadApi
    private lateinit var mockWebServer: okhttp3.mockwebserver.MockWebServer

    @Before
    fun setup() {
        mockWebServer = okhttp3.mockwebserver.MockWebServer()
        mockWebServer.start()

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoodRoadApi::class.java)
    }

    @Test
    fun shouldReturnSuccess_whenResponseIsValid() = runBlocking {
        val mockResponse = """
            {
                "id": "test-id-123",
                "paths": [
                    {
                        "distance": 1250.0,
                        "time": 600000,
                        "points": "encoded_points_string",
                        "points_encoded": true,
                        "route_type": "fast"
                    },
                    {
                        "distance": 1500.0,
                        "time": 720000,
                        "points": "encoded_points_string_2",
                        "points_encoded": true,
                        "route_type": "balanced"
                    },
                    {
                        "distance": 1800.0,
                        "time": 900000,
                        "points": "encoded_points_string_3",
                        "points_encoded": true,
                        "route_type": "safe"
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(okhttp3.mockwebserver.MockResponse()
            .setResponseCode(200)
            .setBody(mockResponse)
            .setHeader("Content-Type", "application/json")
        )

        val request = RouteRequest(
            start = "59.932480,30.262920",
            end = "59.928767,30.264197",
            obstaclePolicies = listOf(RouteObstaclePolicy("STAIRS", 2))
        )

        val response = api.getRoute(request)

        assertNotNull(response)
        assertEquals("test-id-123", response.id)
        assertEquals(3, response.paths.size)
        assertEquals("fast", response.paths[0].routeType)
        assertEquals("balanced", response.paths[1].routeType)
        assertEquals("safe", response.paths[2].routeType)
        assertEquals(1250.0, response.paths[0].distance, 0.1)
        assertEquals(600000, response.paths[0].time)
    }

    @Test(expected = Exception::class)
    fun shouldThrowException_whenServerError(): Unit = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error")
        )

        val request = RouteRequest(
            start = "59.932480,30.262920",
            end = "59.928767,30.264197"
        )

        api.getRoute(request)
    }

    @Test
    fun shouldReturnEmptyPaths_whenResponseHasNoPaths() = runBlocking {
        val mockResponse = """
            {
                "id": "test-id",
                "paths": []
            }
        """.trimIndent()

        mockWebServer.enqueue(okhttp3.mockwebserver.MockResponse()
            .setResponseCode(200)
            .setBody(mockResponse)
            .setHeader("Content-Type", "application/json")
        )

        val request = RouteRequest(
            start = "59.932480,30.262920",
            end = "59.928767,30.264197"
        )

        val response = api.getRoute(request)

        assertNotNull(response)
        assertTrue(response.paths.isEmpty())
    }
}