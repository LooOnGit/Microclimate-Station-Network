package com.example.loofarm.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loofarm.model.Feed
import com.example.loofarm.repository.LooFarmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
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
    fun getFeeds(channelId: Long, apiKey: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getFeeds(channelId = channelId, apiKey = apiKey)
                _feeds.value = response.feeds
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
