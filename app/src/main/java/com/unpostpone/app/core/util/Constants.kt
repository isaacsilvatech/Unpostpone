package com.unpostpone.app.core.util

object Constants {
    const val DATABASE_NAME = "unpostpone_db"
    const val TEMP_UNLOCK_DURATION_MS = 5 * 60 * 1000L

    val DEFAULT_BLOCKED_APPS = listOf(
        BlockableApp("Facebook",    "com.facebook.katana"),
        BlockableApp("Instagram",   "com.instagram.android"),
        BlockableApp("TikTok",      "com.zhiliaoapp.musically"),
        BlockableApp("X (Twitter)", "com.twitter.android"),
        BlockableApp("Kwai",        "com.kwai.video"),
        BlockableApp("YouTube",     "com.google.android.youtube"),
        BlockableApp("Reddit",      "com.reddit.frontpage"),
    )
}

data class BlockableApp(
    val displayName: String,
    val packageName: String
)
