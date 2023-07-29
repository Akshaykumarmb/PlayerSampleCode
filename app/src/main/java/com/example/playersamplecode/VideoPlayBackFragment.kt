package com.example.playersamplecode

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playersamplecode.databinding.FragmentSecondBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaItem.AdsConfiguration
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
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
        val videoUrl = arguments?.getString("videoURL","")
        val videoType = arguments?.getString("videoTYPE","")

        val dataSourceFactory = DefaultDataSource.Factory(view.context);

        val adsLoader = ImaAdsLoader.Builder(view.context).build()

        if(videoType != "hls_ad") {
            exoPlayer = ExoPlayer.Builder(requireContext()).build()
            binding.idExoPlayerVIew.useController = false
        }

        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        if(videoType == "hls"){
            val mediaItem =
                MediaItem.fromUri(Uri.parse(videoUrl))
            val mediaSource = HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
            exoPlayer?.setMediaSource(mediaSource)
        }else if(videoType == "dash"){
            val mediaItem =
                MediaItem.fromUri(Uri.parse(videoUrl))
            val mediaSource = DashMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
            exoPlayer?.setMediaSource(mediaSource)
        }else if(videoType == "hls_ad"){
            val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
                .setAdsLoaderProvider { unusedAdTagUri: AdsConfiguration? -> adsLoader }.setAdViewProvider(binding.idExoPlayerVIew)
            exoPlayer = ExoPlayer.Builder(requireContext()).setMediaSourceFactory(mediaSourceFactory).build()
            adsLoader.setPlayer(exoPlayer!!)
            val mediaItem =
                MediaItem.Builder()
                    .setUri(Uri.parse(videoUrl))
// midrole with 10sec each
                    .setAdsConfiguration(MediaItem.AdsConfiguration.Builder(Uri.parse("https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/vmap_ad_samples&sz=640x480&cust_params=sample_ar%3Dpremidpostlongpod&ciu_szs=300x250&gdfp_req=1&ad_rule=1&output=vmap&unviewed_position_start=1&env=vp&impl=s&cmsid=496&vid=short_onecue&correlator=")).build())
// single mid role                 .setAdsConfiguration(MediaItem.AdsConfiguration.Builder(Uri.parse("https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/vmap_ad_samples&sz=640x480&cust_params=sample_ar%3Dpremidpostpod&ciu_szs=300x250&gdfp_req=1&ad_rule=1&output=vmap&unviewed_position_start=1&env=vp&impl=s&cmsid=496&vid=short_onecue&correlator=")).build())
//prerole,midrole,postrole                    .setAdsConfiguration(MediaItem.AdsConfiguration.Builder(Uri.parse("https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/vmap_ad_samples&sz=640x480&cust_params=sample_ar%3Dpremidpost&ciu_szs=300x250&gdfp_req=1&ad_rule=1&output=vmap&unviewed_position_start=1&env=vp&impl=s&cmsid=496&vid=short_onecue&correlator=")).build())
//prerole           .setAdsConfiguration(MediaItem.AdsConfiguration.Builder(Uri.parse("https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_ad_samples&sz=640x480&cust_params=sample_ct%3Dlinear&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=")).build())
                    .build()
            exoPlayer?.setMediaItem(mediaItem)

        }
        binding.idExoPlayerVIew.player = exoPlayer
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