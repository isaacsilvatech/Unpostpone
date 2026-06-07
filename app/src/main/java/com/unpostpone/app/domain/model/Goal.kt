package com.unpostpone.app.domain.model

data class Goal(
    val id: Long = 0,
    val name: String,
    val targetMinutes: Int,
    val progressMinutes: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val date: String
) {
    val progressPercent: Float
        get() = if (targetMinutes > 0)
            (progressMinutes.toFloat() / targetMinutes).coerceIn(0f, 1f)
        else 0f

    val remainingMinutes: Int
        get() = (targetMinutes - progressMinutes).coerceAtLeast(0)
}
