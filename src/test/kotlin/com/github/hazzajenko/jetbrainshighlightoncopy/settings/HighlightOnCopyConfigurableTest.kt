package com.github.hazzajenko.jetbrainshighlightoncopy.settings

import org.junit.Assert.*
import org.junit.Test

class HighlightOnCopyConfigurableTest {

    @Test
    fun `test getDisplayName returns correct name`() {
        val configurable = HighlightOnCopyConfigurable()
        assertEquals("Highlight on Copy", configurable.displayName)
    }
}
