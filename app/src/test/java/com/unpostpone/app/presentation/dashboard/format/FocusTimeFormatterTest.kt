package com.unpostpone.app.presentation.dashboard.format

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [FocusTimeFormatter]. Pure JUnit4 — no Robolectric, no
 * Android dependencies, runs on the JVM.
 */
class FocusTimeFormatterTest {

    // ── formatFocusTime ──────────────────────────────────────────────────

    @Test
    fun `formatFocusTime — 0 minutes returns just 0m`() {
        assertEquals("0m", FocusTimeFormatter.formatFocusTime(0))
    }

    @Test
    fun `formatFocusTime — 1 minute returns 1m`() {
        assertEquals("1m", FocusTimeFormatter.formatFocusTime(1))
    }

    @Test
    fun `formatFocusTime — 59 minutes returns 59m (no hours yet)`() {
        assertEquals("59m", FocusTimeFormatter.formatFocusTime(59))
    }

    @Test
    fun `formatFocusTime — 60 minutes rolls over to 1h 0m`() {
        assertEquals("1h 0m", FocusTimeFormatter.formatFocusTime(60))
    }

    @Test
    fun `formatFocusTime — 61 minutes is 1h 1m`() {
        assertEquals("1h 1m", FocusTimeFormatter.formatFocusTime(61))
    }

    @Test
    fun `formatFocusTime — 119 minutes is 1h 59m`() {
        assertEquals("1h 59m", FocusTimeFormatter.formatFocusTime(119))
    }

    @Test
    fun `formatFocusTime — 120 minutes is 2h 0m`() {
        assertEquals("2h 0m", FocusTimeFormatter.formatFocusTime(120))
    }

    @Test
    fun `formatFocusTime — 480 minutes (8h target) is 8h 0m`() {
        assertEquals("8h 0m", FocusTimeFormatter.formatFocusTime(480))
    }

    @Test
    fun `formatFocusTime — negative input is defensively clamped to 0`() {
        assertEquals("0m", FocusTimeFormatter.formatFocusTime(-1))
    }

    // ── formatMinutesLabel ───────────────────────────────────────────────

    @Test
    fun `formatMinutesLabel — passes the clamped value to the callback`() {
        val captured = mutableListOf<Int>()
        val result = FocusTimeFormatter.formatMinutesLabel(5) { qty ->
            captured.add(qty)
            "${qty} min"
        }
        assertEquals("5 min", result)
        assertEquals(listOf(5), captured)
    }

    @Test
    fun `formatMinutesLabel — negative input is defensively clamped to 0`() {
        val result = FocusTimeFormatter.formatMinutesLabel(-30) { qty ->
            "${qty} min"
        }
        assertEquals("0 min", result)
    }

    @Test
    fun `formatMinutesLabel — callback receives the raw positive value`() {
        val result = FocusTimeFormatter.formatMinutesLabel(125) { qty ->
            "${qty} min"
        }
        assertEquals("125 min", result)
    }
}
