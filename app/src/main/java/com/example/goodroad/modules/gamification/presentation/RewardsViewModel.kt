package com.example.goodroad.modules.gamification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodroad.modules.gamification.data.LeaderboardItem
import com.example.goodroad.modules.gamification.data.PointTransaction
import com.example.goodroad.modules.gamification.data.PurchaseResponse
import com.example.goodroad.modules.gamification.data.RewardOffer
import com.example.goodroad.modules.gamification.data.RewardsAccount
import com.example.goodroad.modules.gamification.data.RewardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RewardsViewModel(
    private val repository: RewardsRepository
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val rewards: List<RewardOffer> = emptyList(),
        val account: RewardsAccount? = null,
        val history: List<PointTransaction> = emptyList(),
        val leaderboard: List<LeaderboardItem> = emptyList(),
        val purchaseResult: PurchaseResponse? = null,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun loadRewards(
        minPrice: Int? = null,
        maxPrice: Int? = null,
        sort: String = "price_asc"
    ) {
        viewModelScope.launch {
            runCatching {
                _state.value = _state.value.copy(loading = true, error = null)
                repository.getRewards(minPrice, maxPrice, sort)
            }.onSuccess {
                _state.value = _state.value.copy(
                    loading = false,
                    rewards = it
                )
            }.onFailure {
                _state.value = _state.value.copy(
                    loading = false,
                    error = it.message
                )
            }
        }
    }

    fun loadAccount() {
        viewModelScope.launch {
            runCatching { repository.getAccount() }
                .onSuccess {
                    _state.value = _state.value.copy(account = it)
                }
                .onFailure {
                    _state.value = _state.value.copy(error = it.message)
                }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            runCatching { repository.getHistory() }
                .onSuccess {
                    _state.value = _state.value.copy(history = it)
                }
                .onFailure {
                    _state.value = _state.value.copy(error = it.message)
                }
        }
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            runCatching { repository.getLeaderboard() }
                .onSuccess {
                    _state.value = _state.value.copy(leaderboard = it)
                }
                .onFailure {
                    _state.value = _state.value.copy(error = it.message)
                }
        }
    }

    fun purchaseReward(rewardId: String) {
        viewModelScope.launch {
            runCatching {
                repository.purchaseReward(rewardId)
            }.onSuccess {
                _state.value = _state.value.copy(
                    purchaseResult = it
                )
                loadAccount()
                loadHistory()
            }.onFailure {
                _state.value = _state.value.copy(
                    error = it.message
                )
            }
        }
    }
}