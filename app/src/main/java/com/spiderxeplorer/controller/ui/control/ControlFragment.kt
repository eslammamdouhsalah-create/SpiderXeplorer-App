package com.spiderxeplorer.controller.ui.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.spiderxeplorer.controller.MainViewModel
import com.spiderxeplorer.controller.ble.BleManager
import com.spiderxeplorer.controller.databinding.FragmentControlBinding

class ControlFragment : Fragment() {

    private var _binding: FragmentControlBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var bleManager: BleManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bleManager = BleManager.getInstance(requireContext(), viewModel)
        setupObservers()
        setupControlButtons()
        setupSpeedSlider()
    }

    private fun setupObservers() {
        viewModel.connectionState.observe(viewLifecycleOwner) { state ->
            // Update Bluetooth status LED (e.g., change color or icon)
            binding.bluetoothStatus.text = state.toString()
            // Logic to change LED color based on state (e.g., using a custom view or drawable)
        }
    }

    private fun setupControlButtons() {
        // Send command to BLE Manager on button click
        binding.btnForward.setOnClickListener { bleManager.sendCommand("F") }
        binding.btnBackward.setOnClickListener { bleManager.sendCommand("B") }
        binding.btnLeft.setOnClickListener { bleManager.sendCommand("L") }
        binding.btnRight.setOnClickListener { bleManager.sendCommand("R") }
        binding.btnStop.setOnClickListener { bleManager.sendCommand("S") }
    }

    private fun setupSpeedSlider() {
        binding.speedSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.speedValue.text = "$progress%"
                if (fromUser) {
                    // Send speed command (e.g., "V50" for 50%)
                    bleManager.sendCommand("V$progress")
                    viewModel.updateRobotSpeed(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
