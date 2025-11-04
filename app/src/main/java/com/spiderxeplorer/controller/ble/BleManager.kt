package com.spiderxeplorer.controller.ble

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.spiderxeplorer.controller.MainViewModel
import com.spiderxeplorer.controller.MainViewModel.SensorData
import java.util.*

// Constants for BLE UUIDs (MUST be replaced with actual robot UUIDs)
private val SERVICE_UUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB")
private val CHARACTERISTIC_WRITE_UUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")
private val CHARACTERISTIC_NOTIFY_UUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")
private val CCC_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

enum class BleConnectionState {
    DISCONNECTED, SCANNING, CONNECTING, CONNECTED, ERROR
}

class BleManager private constructor(private val context: Context, private val viewModel: MainViewModel) {

    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var notifyCharacteristic: BluetoothGattCharacteristic? = null

    companion object {
        @Volatile
        private var INSTANCE: BleManager? = null

        fun getInstance(context: Context, viewModel: MainViewModel): BleManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BleManager(context.applicationContext, viewModel).also { INSTANCE = it }
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.i("BleManager", "Connected to GATT server.")
                    viewModel.updateConnectionState(BleConnectionState.CONNECTED)
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i("BleManager", "Disconnected from GATT server.")
                    viewModel.updateConnectionState(BleConnectionState.DISCONNECTED)
                    close()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(SERVICE_UUID)
                writeCharacteristic = service?.getCharacteristic(CHARACTERISTIC_WRITE_UUID)
                notifyCharacteristic = service?.getCharacteristic(CHARACTERISTIC_NOTIFY_UUID)

                if (notifyCharacteristic != null) {
                    enableNotifications(gatt, notifyCharacteristic!!)
                }
            } else {
                Log.w("BleManager", "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            // Data received from the robot (Sensor Data)
            val data = characteristic.value
            if (data != null && data.isNotEmpty()) {
                val sensorString = String(data, Charsets.UTF_8)
                Log.d("BleManager", "Received: $sensorString")
                parseSensorData(sensorString)
            }
        }
    }

    private fun enableNotifications(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        gatt.setCharacteristicNotification(characteristic, true)
        val descriptor = characteristic.getDescriptor(CCC_DESCRIPTOR_UUID)
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        gatt.writeDescriptor(descriptor)
    }

    private fun parseSensorData(data: String) {
        // Example format: "T:25.5,D:1.50,M:1,L:500,H:45.2"
        try {
            val parts = data.split(",")
            val sensorMap = parts.associate {
                val (key, value) = it.split(":")
                key to value
            }

            val sensorData = SensorData(
                temperature = sensorMap["T"]?.toFloatOrNull() ?: 0.0f,
                distance = sensorMap["D"]?.toFloatOrNull() ?: 0.0f,
                metalDetected = sensorMap["M"] == "1",
                lightLevel = sensorMap["L"]?.toIntOrNull() ?: 0,
                humidity = sensorMap["H"]?.toFloatOrNull() ?: 0.0f
            )
            viewModel.updateSensorData(sensorData)
        } catch (e: Exception) {
            Log.e("BleManager", "Error parsing sensor data: $data", e)
        }
    }

    fun startScan() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            viewModel.updateConnectionState(BleConnectionState.ERROR)
            return
        }
        viewModel.updateConnectionState(BleConnectionState.SCANNING)
        bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            // Replace "SpiderXeplorer" with the actual name of your robot's BLE device
            if (result.device.name == "SpiderXeplorer") {
                bluetoothAdapter?.bluetoothLeScanner?.stopScan(this)
                connect(result.device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BleManager", "Scan failed: $errorCode")
            viewModel.updateConnectionState(BleConnectionState.ERROR)
        }
    }

    private fun connect(device: BluetoothDevice) {
        viewModel.updateConnectionState(BleConnectionState.CONNECTING)
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    fun sendCommand(command: String) {
        if (bluetoothGatt == null || writeCharacteristic == null) {
            Log.e("BleManager", "GATT or Characteristic not initialized.")
            return
        }

        writeCharacteristic?.let { characteristic ->
            characteristic.value = command.toByteArray(Charsets.UTF_8)
            bluetoothGatt?.writeCharacteristic(characteristic)
            Log.d("BleManager", "Command sent: $command")
        }
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    fun close() {
        bluetoothGatt?.close()
        bluetoothGatt = null
        writeCharacteristic = null
        notifyCharacteristic = null
    }
}

// Placeholder for the actual BLE Service (not strictly needed for this simple manager, but good practice)
class BluetoothLeService : android.app.Service() {
    override fun onBind(intent: android.content.Intent?): android.os.IBinder? {
        return null
    }
}
