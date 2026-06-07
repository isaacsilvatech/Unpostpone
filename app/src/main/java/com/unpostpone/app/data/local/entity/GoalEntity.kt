package com.unpostpone.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetMinutes: Int,
    val progressMinutes: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val date: String // ISO date: yyyy-MM-dd
)
