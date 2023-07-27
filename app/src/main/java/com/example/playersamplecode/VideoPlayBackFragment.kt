package com.example.playersamplecode

import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playersamplecode.databinding.FragmentSecondBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class VideoPlayBackFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private var exoPlayer: ExoPlayer?=null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val TAG = "VideoPlayBackFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }


        val videoUrl = arguments?.getString("videoURL","")
        val videoType = arguments?.getString("videoTYPE","")


        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        binding.idExoPlayerVIew.player = exoPlayer
        binding.idExoPlayerVIew.useController = false
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaItem =
            MediaItem.fromUri(Uri.parse(videoUrl))
        if(videoType == "hls"){
            val mediaSource = HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
            exoPlayer?.setMediaSource(mediaSource)
        }else if(videoType == "dash"){
            val mediaSource = DashMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
            exoPlayer?.setMediaSource(mediaSource)
        }
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true
        exoPlayer?.addListener(object : Listener{
             override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                 when (playbackState) {
                     STATE_BUFFERING -> {
                         Log.d(TAG, "onPlayerStateChanged: buffer-"+exoPlayer?.isPlaying)
                         binding.progressBarCyclic.visibility = View.VISIBLE
                     }
                     STATE_ENDED -> {
                         Log.d(TAG, "onPlayerStateChanged: end-"+exoPlayer?.isPlaying)
                     }
                     STATE_IDLE -> {
                         Log.d(TAG, "onPlayerStateChanged: idle-"+exoPlayer?.isPlaying)
                     }
                     STATE_READY -> {
                         Log.d(TAG, "onPlayerStateChanged: ready-"+exoPlayer?.isPlaying)
                         binding.progressBarCyclic.visibility = View.GONE
                     }
                     else -> {}
                 }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer?.stop()
        exoPlayer?.release()
        _binding = null
    }
}