package com.example.goodroad.modules.tasks.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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

    @POST("tasks/{taskId}/targets/{targetId}/complete")
    suspend fun completeTarget(
        @Path("taskId") taskId: String,
        @Path("targetId") targetId: String
    )
}