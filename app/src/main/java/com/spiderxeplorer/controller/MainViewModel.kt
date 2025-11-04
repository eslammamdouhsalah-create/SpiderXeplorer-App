package com.spiderxeplorer.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spiderxeplorer.controller.ble.BleConnectionState
import com.spiderxeplorer.controller.data.SensorData

class MainViewModel : ViewModel() {

    // LiveData for Bluetooth Connection Status
    private val _connectionState = MutableLiveData(BleConnectionState.DISCONNECTED)
    val connectionState: LiveData<BleConnectionState> = _connectionState

    // LiveData for Sensor Data
    private val _sensorData = MutableLiveData(SensorData())
    val sensorData: LiveData<SensorData> = _sensorData

    // LiveData for Robot Speed
    private val _robotSpeed = MutableLiveData(0)
    val robotSpeed: LiveData<Int> = _robotSpeed

    // --- Public Methods to Update State ---

    fun updateConnectionState(newState: BleConnectionState) {
        _connectionState.value = newState
    }

    fun updateSensorData(data: SensorData) {
        _sensorData.value = data
    }

    fun updateRobotSpeed(speed: Int) {
        _robotSpeed.value = speed
    }

    // --- Data Classes ---

    data class SensorData(
        val temperature: Float = 0.0f,
        val distance: Float = 0.0f,
        val metalDetected: Boolean = false,
        val lightLevel: Int = 0,
        val humidity: Float = 0.0f
    )
}
