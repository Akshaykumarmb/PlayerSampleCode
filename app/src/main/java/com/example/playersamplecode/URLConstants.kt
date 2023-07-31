package com.example.playersamplecode

object URLConstants {

    val hls_url = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8"

    val dash_url = "https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd"
// working url hls --
// Live - https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8
// VOD - https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8

// working url dash --
// Live - https://livesim.dashif.org/livesim/chunkdur_1/ato_7/testpic4_8s/Manifest.mpd
// VOD - https://dash.akamaized.net/dash264/TestCasesUHD/2b/11/MultiRate.mpd
//       https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd

    const val mContentUri =
        "https://media.axprod.net/TestVectors/v7-MultiDRM-SingleKey/Manifest_1080p.mpd"
    const val mLicenseServer = "https://drm-widevine-licensing.axprod.net/AcquireLicense"
    const val mLicenseToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2ZXJzaW9uIjogMSwiY29tX2tleV9pZCI6ICI3YjVkYmIwNC0xYzY3LTRlNjQtYWIyZS1hZTIyMDBkZmY5NzAiLCJtZXNzYWdlIjogeyAgInR5cGUiOiAiZW50aXRsZW1lbnRfbWVzc2FnZSIsICAidmVyc2lvbiI6IDIsICAiY29udGVudF9rZXlzX3NvdXJjZSI6IHsgICAgImlubGluZSI6IFsgICAgICB7ICAgICAgICAiaWQiOiAiOWViNDA1MGQtZTQ0Yi00ODAyLTkzMmUtMjdkNzUwODNlMjY2IiwgICAgICAgICJlbmNyeXB0ZWRfa2V5IjogInZzRTdGakNWZE83T2VpQ21xNFhKUmc9PSIgICAgICB9ICAgIF0gIH19fQ.WOa2ZjpbMASDEyDfER3y0A5K1JzV0bcRo6qig7Zfb7I"
}