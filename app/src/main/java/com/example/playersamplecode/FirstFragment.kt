package com.example.playersamplecode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.playersamplecode.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.hlsPlayback.setOnClickListener {
            val videoUrl = URLConstants.hls_url
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundleOf("videoURL" to videoUrl,"videoTYPE" to "hls"))
        }

        binding.dashDrmPlayback.setOnClickListener {
            val videoUrl = URLConstants.hls_url
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundleOf("videoURL" to videoUrl,"videoTYPE" to "drm"))
        }

        binding.dashPlayback.setOnClickListener {
            val videoUrl = URLConstants.dash_url
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundleOf("videoURL" to videoUrl,"videoTYPE" to "dash"))
        }


        binding.hlsWithAdPlayback.setOnClickListener {
            val videoUrl = URLConstants.hls_url
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundleOf("videoURL" to videoUrl,"videoTYPE" to "hls_ad"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}