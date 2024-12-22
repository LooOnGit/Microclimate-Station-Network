package com.example.loofarm.ui.history_value

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loofarm.model.Feed
import com.example.loofarm.model.ThingSpeakManager
import com.example.loofarm.repository.LooFarmRepository
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val repository = LooFarmRepository()

    // LiveData to hold the API response
    private val _feeds = MutableLiveData<List<Feed>?>()
    val feeds: LiveData<List<Feed>?> = _feeds

    // LiveData to handle loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // LiveData to handle errors
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Function to fetch data from API
    fun getFeeds() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getThingSpeakData(
                    channelId = ThingSpeakManager.channelId,
                    apiKey = ThingSpeakManager.apiKey,
                    results = 100
                )
                _feeds.value = response.feeds
            } catch (e: Exception) {
                _error.value = e.message
                _feeds.value = null
            } finally {
                _loading.value = false
            }
        }
    }

    fun getFeedsAI(result: Int) {
        Log.d(TAG, "getFeedsAI() called with: result = $result")
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getThingSpeakDataAI(
                    channelId = ThingSpeakManager.channelIdAI,
                    results = result
                )
                _feeds.value = response.feeds
            } catch (e: Exception) {
                _error.value = e.message
                _feeds.value = null
            } finally {
                _loading.value = false
            }
        }
    }

    companion object {
        private val TAG by lazy { HistoryViewModel::class.java.simpleName }
    }
}
