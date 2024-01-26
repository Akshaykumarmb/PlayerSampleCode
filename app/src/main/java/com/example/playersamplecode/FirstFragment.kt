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

    private var firstBinding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firstBinding = FragmentFirstBinding.inflate(inflater, container, false)
        return firstBinding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstBinding?.hlsPlayback?.setOnClickListener {
            val videoUrl = URLConstants.hls_url
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundleOf("videoURL" to videoUrl,"videoTYPE" to "hls"))
        }

        firstBinding?.dashDrmPlayback?.setOnClickListener {
            val videoUrl = URLConstants.drm_url
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundleOf("videoURL" to videoUrl,"videoTYPE" to "drm"))
        }

        firstBinding?.dashDrmWithSubtitlePlayback?.setOnClickListener {
            val videoUrl = URLConstants.drm_url
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundleOf("videoURL" to videoUrl,"videoTYPE" to "drm_subtitle"))
        }

        firstBinding?.dashPlayback?.setOnClickListener {
            val videoUrl = URLConstants.dash_url
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundleOf("videoURL" to videoUrl,"videoTYPE" to "dash"))
        }


        firstBinding?.hlsWithAdPlayback?.setOnClickListener {
            val videoUrl = URLConstants.hls_url
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundleOf("videoURL" to videoUrl,"videoTYPE" to "hls_ad"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firstBinding = null
    }
}