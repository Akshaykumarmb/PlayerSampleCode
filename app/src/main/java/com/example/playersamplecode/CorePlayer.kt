package com.example.playersamplecode

import android.content.Context
import android.net.Uri
import com.example.playersamplecode.URLConstants.drm_licence
import com.example.playersamplecode.URLConstants.drm_url
import com.example.playersamplecode.databinding.FragmentSecondBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.MediaItem.AdsConfiguration
import com.google.android.exoplayer2.drm.DefaultDrmSessionManagerProvider
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.ext.ima.ImaServerSideAdInsertionMediaSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.common.collect.ImmutableList

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
                exoPlayer = loadDRMContent(context,videoUrl)
                binding.idExoPlayerVIew.useController = false
            }
        }
        return exoPlayer
    }

    private fun loadPlayerForHLSorDASH(context: Context, videoUrl:String, videoType:String): ExoPlayer? {
        val exoPlayer = ExoPlayer.Builder(context).build()
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()

        val assetSrtUri = Uri.parse(("file:///android_asset/subtitle.srt"))
        val subtitle = MediaItem.SubtitleConfiguration.Builder(assetSrtUri)
            .setMimeType(MimeTypes.APPLICATION_SUBRIP)
            .setLanguage("en")
            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
            .build()

        val mediaItem =MediaItem.Builder()
           .setUri(videoUrl)
            .setSubtitleConfigurations(ImmutableList.of(subtitle))
            .build()

        var mediaSource = if(videoType == "hls") {
            HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
        }else{
            DashMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
        }
        exoPlayer?.setMediaSource(mediaSource)
        return exoPlayer
    }

    private fun loadPlayerForHlsAds(context: Context, binding: FragmentSecondBinding, videoUrl: String): ExoPlayer? {
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

    private fun loadDRMContent(context: Context,videoUrl: String): ExoPlayer? {

       val mediaItem=MediaItem.Builder()
           .setUri(Uri.parse(videoUrl))
           .setMediaMetadata(MediaMetadata.Builder().setTitle("Widevine DASH cenc: Tears").build())
           .setMimeType("application/dash+xml")
           .setDrmConfiguration(MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                   .setLicenseUri(drm_licence)
                   .build())
           .build()

        val drmSessionManagerProvider = DefaultDrmSessionManagerProvider()
        drmSessionManagerProvider.setDrmHttpDataSourceFactory(
            DefaultHttpDataSource.Factory()
        )
        val factory=DefaultMediaSourceFactory(context)
            .setDrmSessionManagerProvider(drmSessionManagerProvider)

        exoPlayer = ExoPlayer.Builder(context).setMediaSourceFactory(factory).build()
        exoPlayer?.setMediaItem(mediaItem)

        return exoPlayer
    }



}