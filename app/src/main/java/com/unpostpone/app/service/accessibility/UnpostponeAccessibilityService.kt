package com.unpostpone.app.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.unpostpone.app.domain.usecase.blockedapp.IsAppBlockedUseCase
import com.unpostpone.app.domain.usecase.blockedapp.ObserveBlockingEnabledUseCase
import com.unpostpone.app.domain.usecase.statistics.IncrementBlockCountUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class UnpostponeAccessibilityService : AccessibilityService() {

    @Inject lateinit var isAppBlockedUseCase: IsAppBlockedUseCase
    @Inject lateinit var incrementBlockCountUseCase: IncrementBlockCountUseCase
    @Inject lateinit var observeBlockingEnabledUseCase: ObserveBlockingEnabledUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Package allowed for a 5-minute temporary bypass. */
    @Volatile private var temporarilyUnlockedPackage: String? = null

    override fun onServiceConnected() {
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            notificationTimeout = 100
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val packageName = event.packageName?.toString() ?: return
        if (packageName == applicationContext.packageName) return
        if (packageName == temporarilyUnlockedPackage) return

        serviceScope.launch {
            // Global gate: if the user has the master toggle off, do nothing
            // — no per-app lookup, no block count, no blocker screen.
            if (!observeBlockingEnabledUseCase().first()) return@launch

            if (isAppBlockedUseCase(packageName)) {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                incrementBlockCountUseCase(today)
                withContext(Dispatchers.Main) { launchBlockerActivity(packageName) }
            }
        }
    }

    private fun launchBlockerActivity(packageName: String) {
        packageManager.getLaunchIntentForPackage(applicationContext.packageName)
            ?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(EXTRA_BLOCKED_PACKAGE, packageName)
            }
            ?.let { startActivity(it) }
    }

    fun grantTemporaryUnlock(packageName: String) {
        temporarilyUnlockedPackage = packageName
        serviceScope.launch {
            delay(5 * 60 * 1_000L)
            temporarilyUnlockedPackage = null
        }
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val EXTRA_BLOCKED_PACKAGE = "extra_blocked_package"
    }
}
