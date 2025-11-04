package com.spiderxeplorer.controller.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.spiderxeplorer.controller.MainViewModel
import com.spiderxeplorer.controller.ble.BleManager
import com.spiderxeplorer.controller.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var bleManager: BleManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bleManager = BleManager.getInstance(requireContext(), viewModel)
        setupListeners()
    }

    private fun setupListeners() {
        // Reset Bluetooth
        binding.btnResetBluetooth.setOnClickListener {
            bleManager.disconnect()
            Toast.makeText(context, "Bluetooth connection reset. Please reconnect.", Toast.LENGTH_SHORT).show()
        }

        // Macro Commands (Placeholder)
        binding.btnMacroCommands.setOnClickListener {
            // TODO: Implement a dialog or new fragment for Macro creation/editing
            Toast.makeText(context, "Macro Commands feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Theme Selection (Placeholder)
        binding.btnTheme.setOnClickListener {
            // TODO: Implement logic to change theme (e.g., using SharedPreferences and recreating activity)
            Toast.makeText(context, "Theme selection not implemented yet.", Toast.LENGTH_SHORT).show()
        }

        // App Info
        binding.appVersion.text = "Version 1.0 (Build 1)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
