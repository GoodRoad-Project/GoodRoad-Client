package com.example.goodroad.modules.tasks.data

class TasksRepository(
    private val api: TasksApi
) {

    suspend fun loadTasks(
        activityType: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ): List<TaskViewDto> {
        return api.getTasks(
            activityType = activityType,
            latitude = latitude,
            longitude = longitude
        )
    }

    suspend fun loadCompletedTasks(): List<CompletedTaskDto> {
        return api.getCompletedTasks()
    }

    suspend fun completeTarget(taskId: String, targetId: String) {
        return api.completeTarget(taskId, targetId)
    }
}