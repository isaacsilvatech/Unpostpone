package com.unpostpone.app.core.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

object AppLabelResolver {
    fun resolve(context: Context, packageName: String): String {
        val pm = context.packageManager
        return try {
            pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
        } catch (_: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    fun resolveIcon(context: Context, packageName: String): Drawable? {
        val pm = context.packageManager
        return try {
            pm.getApplicationIcon(packageName)
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }
}
