package com.unpostpone.app.presentation.reblock

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.unpostpone.app.service.tempunlock.TemporaryUnlockService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ReBlockViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    fun reBlockNow() {
        val intent = Intent(context, TemporaryUnlockService::class.java)
            .setAction(TemporaryUnlockService.ACTION_STOP)
        context.startService(intent)
    }
}
