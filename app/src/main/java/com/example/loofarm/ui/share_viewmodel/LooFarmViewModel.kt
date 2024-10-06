package com.example.loofarm.ui.share_viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loofarm.model.ThingSpeakResponse
import com.example.loofarm.repository.LooFarmRepository
import kotlinx.coroutines.launch

class LooFarmViewModel : ViewModel() {
    private val repository = LooFarmRepository()

    // LiveData to hold the API response
    private val _thingSpeakResponse = MutableLiveData<ThingSpeakResponse?>()
    val thingSpeakResponse: LiveData<ThingSpeakResponse?> = _thingSpeakResponse

    // LiveData to handle loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // LiveData to handle errors
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Function to fetch data from API
    fun getThingSpeakData(channelId: Long, apiKey: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getThingSpeakData(channelId = channelId, apiKey = apiKey)
                _thingSpeakResponse.value = response
            } catch (e: Exception) {
                _error.value = e.message
                _thingSpeakResponse.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}
