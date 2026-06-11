package com.unpostpone.app.core.util

import android.icu.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {

    private val isoParser = ThreadLocal.withInitial {
        SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }

    fun formatForUser(isoDate: String): String {
        val date = parseIso(isoDate) ?: return isoDate
        val formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        return formatter.format(date)
    }

    private fun parseIso(isoDate: String): Date? = try {
        isoParser.get()!!.parse(isoDate)
    } catch (_: ParseException) {
        null
    }
}
