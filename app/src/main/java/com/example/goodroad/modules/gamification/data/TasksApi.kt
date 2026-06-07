package com.example.goodroad.modules.gamification.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TasksApi {

    @GET("tasks")
    suspend fun getTasks(
        @Query("activityType") activityType: String? = null,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null
    ): List<TaskViewDto>

    @GET("tasks/completed")
    suspend fun getCompletedTasks(): List<CompletedTaskDto>

    @POST("tasks")
    suspend fun createTask(
        @Body request: TaskCreateReq
    ): TaskViewDto
}