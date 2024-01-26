package com.example.playersamplecode

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playersamplecode.databinding.FragmentSecondBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.MediaItem.AdsConfiguration
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.offline.DownloadHelper.createMediaSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import okhttp3.internal.userAgent


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

        init()
        startPlayer()

    }

    private fun init(){
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        binding.idExoPlayerVIew.setOnClickListener {
            binding.playerControlLayout.visibility = View.VISIBLE
            Handler().postDelayed({
                binding.playerControlLayout.visibility = View.GONE
            }, 5000)
        }

        binding.playPause.setOnClickListener {
            if(exoPlayer?.isPlaying == true){
                exoPlayer?.pause()
                binding.playPause.setImageDrawable(context?.getDrawable(R.drawable.ic_media_play_icon))
            }else{
                exoPlayer?.play()
                binding.playPause.setImageDrawable(context?.getDrawable(R.drawable.ic_media_pause_icon))
            }
        }
    }


    private fun startPlayer(){
        val videoUrl = arguments?.getString("videoURL","")
        val videoType = arguments?.getString("videoTYPE","")
        exoPlayer = CorePlayer().initplayer(requireContext(),videoUrl.toString(),videoType.toString(),binding)
        binding.idExoPlayerVIew.player = exoPlayer
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true
        startPlayerListner()
    }

    private fun startPlayerListner(){
        exoPlayer?.addListener(object : Listener{
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
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