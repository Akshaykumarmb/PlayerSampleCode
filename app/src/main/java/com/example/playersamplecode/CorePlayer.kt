package com.example.playersamplecode

import android.content.Context
import android.net.Uri
import com.example.playersamplecode.databinding.FragmentSecondBinding
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util

class CorePlayer {

    private var exoPlayer: ExoPlayer?=null

    fun initplayer(context: Context,videoUrl:String,videoType:String,binding: FragmentSecondBinding): ExoPlayer? {
        when (videoType) {
            "dash", "hls" -> {
                exoPlayer = loadPlayerForHLSorDASH(context,videoUrl,videoType)
                binding.idExoPlayerVIew.useController = false
            }
            "hls_ad" -> {
                exoPlayer = loadPlayerForHlsAds(context,binding,videoUrl)
            }
            "drm" -> {
                exoPlayer = loadDRMContent(context)
            }
        }
        return exoPlayer
    }

    fun loadPlayerForHLSorDASH(context: Context,videoUrl:String,videoType:String): ExoPlayer? {
        val exoPlayer = ExoPlayer.Builder(context).build()
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaItem =
            MediaItem.fromUri(Uri.parse(videoUrl))
        var mediaSource = if(videoType == "hls") {
            HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
        }else{
            DashMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
        }
        exoPlayer?.setMediaSource(mediaSource)
        return exoPlayer
    }

    fun loadPlayerForHlsAds(context: Context,binding: FragmentSecondBinding,videoUrl: String): ExoPlayer? {
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val adsLoader = ImaAdsLoader.Builder(context).build()
        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
            .setAdsLoaderProvider { unusedAdTagUri: MediaItem.AdsConfiguration? -> adsLoader }
            .setAdViewProvider(binding.idExoPlayerVIew)
        exoPlayer = ExoPlayer.Builder(context).setMediaSourceFactory(mediaSourceFactory).build()
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
        return exoPlayer
    }

    fun loadDRMContent(context: Context,): ExoPlayer? {
        val dataSourceFactory = DefaultDataSource.Factory(context)

        val userAgent = Util.getUserAgent(context, "DRM example app")
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory().setUserAgent(userAgent)
        val drmCallback = HttpMediaDrmCallback(
            URLConstants.mLicenseServer,
            defaultHttpDataSourceFactory
        )

        // Here the license token is attached to license request
        if (URLConstants.mLicenseToken != null) {
            drmCallback.setKeyRequestProperty("X-AxDRM-Message", URLConstants.mLicenseToken)
        }

        val builddermSession = DefaultDrmSessionManager.Builder()
            .setUuidAndExoMediaDrmProvider(C.WIDEVINE_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
            .build(drmCallback)

        val mediasource = DashMediaSource.Factory(DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory)
            .setDrmSessionManagerProvider{builddermSession}
            .createMediaSource(MediaItem.fromUri(Uri.parse(URLConstants.mContentUri)))

        exoPlayer?.setMediaSource(mediasource)
        return exoPlayer
    }



}