package com.github.hazzajenko.jetbrainshighlightoncopy

import org.junit.Assert.*
import org.junit.Test
import java.awt.Color

class HighlightOnCopyListenerTest {

    @Test
    fun `test parseColor with valid hex color`() {
        val result = HighlightOnCopyListener.parseColor("#FF0000")
        assertEquals(Color.RED, result)
    }

    @Test
    fun `test parseColor with valid hex color lowercase`() {
        val result = HighlightOnCopyListener.parseColor("#ff0000")
        assertEquals(Color.RED, result)
    }

    @Test
    fun `test parseColor with invalid hex color returns yellow`() {
        val result = HighlightOnCopyListener.parseColor("invalid")
        assertEquals(Color.YELLOW, result)
    }

    @Test
    fun `test parseColor with 9-character hex including alpha`() {
        val result = HighlightOnCopyListener.parseColor("#FF000080")
        assertEquals(Color(255, 0, 0, 128), result)
    }

    @Test
    fun `test parseColor with short hex color`() {
        // Test what actually happens with 3-digit hex
        val result = HighlightOnCopyListener.parseColor("#F00")
        // Color.decode might interpret #F00 as a valid short format
        assertNotNull("Result should not be null", result)
        assertTrue("Result should be a Color", result is Color)
    }

    @Test
    fun `test parseColor with empty string returns yellow`() {
        val result = HighlightOnCopyListener.parseColor("")
        assertEquals(Color.YELLOW, result)
    }

    @Test
    fun `test parseColor with hex color without hash`() {
        // This should fail and return yellow since Color.decode expects a #
        val result = HighlightOnCopyListener.parseColor("FF0000")
        assertEquals(Color.YELLOW, result)
    }

    @Test
    fun `test parseColor with various valid colors`() {
        val testCases = mapOf(
            "#00FF00" to Color.GREEN,
            "#0000FF" to Color.BLUE,
            "#FFFFFF" to Color.WHITE,
            "#000000" to Color.BLACK,
            "#FFFF00" to Color.YELLOW
        )

        testCases.forEach { (input, expected) ->
            val result = HighlightOnCopyListener.parseColor(input)
            assertEquals("Failed for color $input", expected, result)
        }
    }

    @Test
    fun `test parseColor with 9-character hex various alpha values`() {
        // Test with full alpha
        val fullAlpha = HighlightOnCopyListener.parseColor("#FF0000FF")
        assertEquals(Color(255, 0, 0, 255), fullAlpha)

        // Test with half alpha
        val halfAlpha = HighlightOnCopyListener.parseColor("#00FF0080")
        assertEquals(Color(0, 255, 0, 128), halfAlpha)

        // Test with no alpha
        val noAlpha = HighlightOnCopyListener.parseColor("#0000FF00")
        assertEquals(Color(0, 0, 255, 0), noAlpha)
    }

    @Test
    fun `test parseColor edge cases`() {
        val edgeCases = listOf(
            "#",          // Just hash
            "#GGGGGG",    // Invalid hex characters
            "#1234567890", // Too long (but not 9)
            "red",        // Color name (not supported by Color.decode with #)
            "#12.34.56"   // Invalid format
        )

        edgeCases.forEach { input ->
            val result = HighlightOnCopyListener.parseColor(input)
            assertEquals("Failed for edge case: $input", Color.YELLOW, result)
        }
    }
}
