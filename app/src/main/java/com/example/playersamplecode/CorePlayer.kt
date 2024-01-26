package com.example.playersamplecode

import android.content.Context
import android.graphics.Color
import android.net.Uri
import com.example.playersamplecode.URLConstants.drm_licence
import com.example.playersamplecode.URLConstants.drm_url
import com.example.playersamplecode.URLConstants.subtitle_url
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
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.text.Cue.TEXT_SIZE_TYPE_ABSOLUTE
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.ui.CaptionStyleCompat
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
                exoPlayer = loadDRMContent(context,videoUrl,null)
                binding.idExoPlayerVIew.useController = false
            }
            "drm_subtitle"->{
                exoPlayer = loadDRMContent(context,videoUrl,URLConstants.subtitle_url)
                binding.idExoPlayerVIew.useController = false

                val captionStyleCompat = CaptionStyleCompat(Color.WHITE, Color.parseColor("#5b000000"), Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW, Color.parseColor("#5b000000"), null)
                binding.idExoPlayerVIew.subtitleView?.setStyle(captionStyleCompat)
                binding.idExoPlayerVIew.subtitleView?.setApplyEmbeddedStyles(false)
            }

        }
        return exoPlayer
    }

    private fun loadPlayerForHLSorDASH(context: Context, videoUrl:String, videoType:String): ExoPlayer? {
        val exoPlayer = ExoPlayer.Builder(context).build()
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()

        val mediaItem =MediaItem.Builder()
           .setUri(videoUrl)
            .build()

        var mediaSource = if(videoType == "hls") {
            HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
        }else{
            DashMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)
        }
        exoPlayer?.setMediaSource(mediaSource)
        exoPlayer?.trackSelectionParameters = exoPlayer.trackSelectionParameters
            .buildUpon()
            .setPreferredTextLanguage("en")
            .build()
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

    private fun loadDRMContent(context: Context,videoUrl: String,subtitle:String?): ExoPlayer? {

        var mediaItem:MediaItem?=null
        if(subtitle!=null && subtitle.isNotEmpty()){

           val subtitleConfiguration= MediaItem.SubtitleConfiguration.Builder(
                Uri.parse(subtitle))
                .setMimeType("text/vtt")
                .setLanguage("en")
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                .build()

             mediaItem=MediaItem.Builder()
                .setUri(Uri.parse(videoUrl))
                .setMediaMetadata(MediaMetadata.Builder().setTitle("Widevine DASH cenc: Tears").build())
                .setMimeType("application/dash+xml")
                .setSubtitleConfigurations(ImmutableList.of(subtitleConfiguration))
                .setDrmConfiguration(MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(drm_licence)
                    .build())
                .build()



        }else{
             mediaItem=MediaItem.Builder()
                .setUri(Uri.parse(videoUrl))
                .setMediaMetadata(MediaMetadata.Builder().setTitle("Widevine DASH cenc: Tears").build())
                .setMimeType("application/dash+xml")
                .setDrmConfiguration(MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(drm_licence)
                    .build())
                .build()
        }

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