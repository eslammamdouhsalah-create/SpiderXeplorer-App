package com.spiderxeplorer.controller.ui.sensors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.spiderxeplorer.controller.MainViewModel
import com.spiderxeplorer.controller.databinding.FragmentSensorsBinding

class SensorsFragment : Fragment() {

    private var _binding: FragmentSensorsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.sensorData.observe(viewLifecycleOwner) { data ->
            binding.tempValue.text = String.format("%.1f °C", data.temperature)
            binding.distanceValue.text = String.format("%.2f m", data.distance)
            binding.lightValue.text = "${data.lightLevel} lux"
            binding.humidityValue.text = String.format("%.1f %%", data.humidity)

            // Metal Detection Alert
            if (data.metalDetected) {
                binding.metalStatus.text = "DETECTED ⚡"
                binding.metalStatus.setTextColor(resources.getColor(com.spiderxeplorer.controller.R.color.red_cyber, null))
                // TODO: Add sound alert logic here
            } else {
                binding.metalStatus.text = "CLEAR"
                binding.metalStatus.setTextColor(resources.getColor(com.spiderxeplorer.controller.R.color.red_dark, null))
            }

            // Distance Alert (Obstacle)
            if (data.distance < 0.5f && data.distance > 0f) { // Less than 50cm
                binding.distanceValue.setTextColor(resources.getColor(com.spiderxeplorer.controller.R.color.red_cyber, null))
                // TODO: Add visual/sound alert for obstacle
            } else {
                binding.distanceValue.setTextColor(resources.getColor(com.spiderxeplorer.controller.R.color.red_dark, null))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
