package com.example.goodroad.modules.gamification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.modules.gamification.data.CompletedTaskDto
import com.example.goodroad.modules.gamification.data.TaskCreateReq
import com.example.goodroad.modules.gamification.data.TaskViewDto
import com.example.goodroad.modules.gamification.data.TasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TasksViewModel(
    private val repository: TasksRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TaskViewDto>>(emptyList())
    val tasks: StateFlow<List<TaskViewDto>> = _tasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<List<CompletedTaskDto>>(emptyList())
    val completedTasks: StateFlow<List<CompletedTaskDto>> = _completedTasks.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadTasks(
        activityType: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                _tasks.value = repository.loadTasks(
                    activityType = activityType,
                    latitude = latitude,
                    longitude = longitude
                )

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadCompletedTasks() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                _completedTasks.value =
                    repository.loadCompletedTasks()

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun createTask(
        request: TaskCreateReq,
        onSuccess: (TaskViewDto) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val created = repository.createTask(request)

                _tasks.value = listOf(created) + _tasks.value

                onSuccess(created)

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}