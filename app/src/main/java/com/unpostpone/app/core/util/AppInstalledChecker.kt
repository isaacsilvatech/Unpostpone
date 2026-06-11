package com.unpostpone.app.core.util

import android.content.Context
import android.content.pm.PackageManager

object AppInstalledChecker {
    fun isInstalled(context: Context, packageName: String): Boolean = try {
        context.packageManager.getApplicationInfo(packageName, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }
}
