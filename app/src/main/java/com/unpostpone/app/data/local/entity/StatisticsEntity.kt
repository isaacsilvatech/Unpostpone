package com.unpostpone.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statistics")
data class StatisticsEntity(
    @PrimaryKey
    val date: String, // ISO date: yyyy-MM-dd
    val focusedMinutes: Int = 0,
    val blockCount: Int = 0,
    val unlockAttempts: Int = 0
)
