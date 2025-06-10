package com.github.hazzajenko.jetbrainshighlightoncopy.settings

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HighlightOnCopySettingsTest {

    private lateinit var settings: HighlightOnCopySettings

    @Before
    fun setUp() {
        settings = HighlightOnCopySettings()
    }

    @Test
    fun `test default settings values`() {
        assertEquals("#E66159", settings.backgroundColor)
        assertEquals("", settings.foregroundColor)
        assertEquals(1000, settings.timeout)
        assertEquals(1, settings.blinkCount)
        assertEquals(150, settings.blinkInterval)
    }

    @Test
    fun `test getState returns self`() {
        val state = settings.getState()
        assertSame(settings, state)
    }

    @Test
    fun `test loadState copies values correctly`() {
        val newState = HighlightOnCopySettings().apply {
            backgroundColor = "#FF0000"
            foregroundColor = "#000000"
            timeout = 2000
            blinkCount = 3
            blinkInterval = 200
        }

        settings.loadState(newState)

        assertEquals("#FF0000", settings.backgroundColor)
        assertEquals("#000000", settings.foregroundColor)
        assertEquals(2000, settings.timeout)
        assertEquals(3, settings.blinkCount)
        assertEquals(200, settings.blinkInterval)
    }

    @Test
    fun `test setting background color`() {
        settings.backgroundColor = "#00FF00"
        assertEquals("#00FF00", settings.backgroundColor)
    }

    @Test
    fun `test setting foreground color`() {
        settings.foregroundColor = "#FFFFFF"
        assertEquals("#FFFFFF", settings.foregroundColor)
    }

    @Test
    fun `test setting timeout`() {
        settings.timeout = 5000
        assertEquals(5000, settings.timeout)
    }

    @Test
    fun `test setting blink count`() {
        settings.blinkCount = 5
        assertEquals(5, settings.blinkCount)
    }

    @Test
    fun `test setting blink interval`() {
        settings.blinkInterval = 300
        assertEquals(300, settings.blinkInterval)
    }

    @Test
    fun `test empty foreground color`() {
        settings.foregroundColor = ""
        assertEquals("", settings.foregroundColor)
    }

    @Test
    fun `test boundary values for timeout`() {
        settings.timeout = 0
        assertEquals(0, settings.timeout)
        
        settings.timeout = Integer.MAX_VALUE
        assertEquals(Integer.MAX_VALUE, settings.timeout)
    }

    @Test
    fun `test boundary values for blink count`() {
        settings.blinkCount = 1
        assertEquals(1, settings.blinkCount)
        
        settings.blinkCount = 10
        assertEquals(10, settings.blinkCount)
    }

    @Test
    fun `test boundary values for blink interval`() {
        settings.blinkInterval = 50
        assertEquals(50, settings.blinkInterval)
        
        settings.blinkInterval = 1000
        assertEquals(1000, settings.blinkInterval)
    }

    @Test
    fun `test state persistence simulation`() {
        // Simulate the persistence behavior
        val originalSettings = HighlightOnCopySettings().apply {
            backgroundColor = "#FF00FF"
            foregroundColor = "#AAAAAA"
            timeout = 3000
            blinkCount = 2
            blinkInterval = 250
        }

        // Get state (what would be persisted)
        val state = originalSettings.getState()

        // Create new instance and load state (what would happen on restart)
        val newSettings = HighlightOnCopySettings()
        newSettings.loadState(state)

        // Verify all values were preserved
        assertEquals(originalSettings.backgroundColor, newSettings.backgroundColor)
        assertEquals(originalSettings.foregroundColor, newSettings.foregroundColor)
        assertEquals(originalSettings.timeout, newSettings.timeout)
        assertEquals(originalSettings.blinkCount, newSettings.blinkCount)
        assertEquals(originalSettings.blinkInterval, newSettings.blinkInterval)
    }

    @Test
    fun `test hex color format validation simulation`() {
        // Test various hex color formats that should work
        val validColors = listOf(
            "#FF0000",  // Standard 6-digit hex
            "#00FF00",
            "#0000FF",
            "#FFFFFF",
            "#000000",
            "#ABCDEF",
            "#123456"
        )

        validColors.forEach { color ->
            settings.backgroundColor = color
            assertEquals(color, settings.backgroundColor)
        }
    }

    @Test
    fun `test foreground color optional behavior`() {
        // Test that foreground color can be empty (optional)
        settings.foregroundColor = ""
        assertEquals("", settings.foregroundColor)
        
        // Test that it can also be set to a color
        settings.foregroundColor = "#000000"
        assertEquals("#000000", settings.foregroundColor)
        
        // Test that it can be reset to empty
        settings.foregroundColor = ""
        assertEquals("", settings.foregroundColor)
    }

    @Test
    fun `test negative values handling`() {
        // Test negative timeout (should be allowed by the class but might be invalid for UI)
        settings.timeout = -100
        assertEquals(-100, settings.timeout)
        
        // Test negative blink count
        settings.blinkCount = -1
        assertEquals(-1, settings.blinkCount)
        
        // Test negative blink interval
        settings.blinkInterval = -50
        assertEquals(-50, settings.blinkInterval)
    }

    @Test
    fun `test string values with special characters`() {
        // Test background color with special characters (should be stored as-is)
        settings.backgroundColor = "#FF0000!"
        assertEquals("#FF0000!", settings.backgroundColor)
        
        // Test foreground color with spaces
        settings.foregroundColor = " #00FF00 "
        assertEquals(" #00FF00 ", settings.foregroundColor)
    }

    @Test
    fun `test multiple state operations`() {
        // Test multiple load/save operations
        val originalSettings = HighlightOnCopySettings().apply {
            backgroundColor = "#111111"
            blinkCount = 5
        }
        
        val state1 = originalSettings.getState()
        
        val newSettings1 = HighlightOnCopySettings()
        newSettings1.loadState(state1)
        assertEquals("#111111", newSettings1.backgroundColor)
        assertEquals(5, newSettings1.blinkCount)
        
        // Test with different values
        val originalSettings2 = HighlightOnCopySettings().apply {
            backgroundColor = "#222222"
            blinkCount = 7
        }
        
        val state2 = originalSettings2.getState()
        
        val newSettings2 = HighlightOnCopySettings()
        newSettings2.loadState(state2)
        assertEquals("#222222", newSettings2.backgroundColor)
        assertEquals(7, newSettings2.blinkCount)
    }
}