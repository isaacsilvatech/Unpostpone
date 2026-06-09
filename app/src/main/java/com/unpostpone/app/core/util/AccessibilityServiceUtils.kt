package com.unpostpone.app.core.util

import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import com.unpostpone.app.service.accessibility.UnpostponeAccessibilityService

object AccessibilityServiceUtils {

    fun isUnpostponeEnabled(context: Context): Boolean {
        val expected = "${context.packageName}/${UnpostponeAccessibilityService::class.java.name}"
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ) ?: return false

        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(enabled)
        while (splitter.hasNext()) {
            if (splitter.next().equals(expected, ignoreCase = true)) return true
        }
        return false
    }
}
