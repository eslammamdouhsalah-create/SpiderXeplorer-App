package com.spiderxeplorer.controller.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.spiderxeplorer.controller.MainViewModel
import com.spiderxeplorer.controller.databinding.FragmentCameraBinding

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupCameraControls()
        // NOTE: Actual video streaming logic (e.g., using a custom library for RTSP/WebRTC)
        // must be implemented here. The PreviewView is a placeholder for the stream.
    }

    private fun setupObservers() {
        viewModel.robotSpeed.observe(viewLifecycleOwner) { speed ->
            binding.overlaySpeed.text = "SPEED: $speed%"
        }
        viewModel.connectionState.observe(viewLifecycleOwner) { state ->
            binding.overlayBtStatus.text = "BT: $state"
        }
        // TODO: Observe direction of movement from ViewModel if implemented
    }

    private fun setupCameraControls() {
        binding.btnCapturePhoto.setOnClickListener {
            // TODO: Implement photo capture logic
        }
        binding.btnRecordVideo.setOnClickListener {
            // TODO: Implement video recording logic
        }
        binding.btnSwitchCamera.setOnClickListener {
            // TODO: Implement camera switch logic (if multiple streams are available)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
