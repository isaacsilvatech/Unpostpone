package com.unpostpone.app.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.unpostpone.app.MainActivity
import com.unpostpone.app.core.tempunlock.TemporaryUnlockManager
import com.unpostpone.app.domain.usecase.blockedapp.IsAppBlockedUseCase
import com.unpostpone.app.domain.usecase.blockedapp.ObserveBlockingEnabledUseCase
import com.unpostpone.app.domain.usecase.statistics.IncrementBlockCountUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class UnpostponeAccessibilityService : AccessibilityService() {

    @Inject lateinit var isAppBlockedUseCase: IsAppBlockedUseCase
    @Inject lateinit var incrementBlockCountUseCase: IncrementBlockCountUseCase
    @Inject lateinit var observeBlockingEnabledUseCase: ObserveBlockingEnabledUseCase
    @Inject lateinit var temporaryUnlockManager: TemporaryUnlockManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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
        if (temporaryUnlockManager.isActive(packageName)) return

        serviceScope.launch {
            if (!observeBlockingEnabledUseCase().first()) return@launch

            if (isAppBlockedUseCase(packageName)) {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                incrementBlockCountUseCase(today)
                withContext(Dispatchers.Main) { launchBlockerActivity(packageName) }
            }
        }
    }

    private fun launchBlockerActivity(packageName: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            component = ComponentName(applicationContext, MainActivity::class.java)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            putExtra(EXTRA_BLOCKED_PACKAGE, packageName)
        }
        startActivity(intent)
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
