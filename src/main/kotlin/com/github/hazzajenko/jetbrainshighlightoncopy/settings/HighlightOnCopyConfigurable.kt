package com.github.hazzajenko.jetbrainshighlightoncopy.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.ui.dsl.builder.bindIntValue
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel

class HighlightOnCopyConfigurable : BoundConfigurable("Highlight on Copy") {
    private val settings by lazy { HighlightOnCopySettings.getInstance() }

    override fun createPanel() = panel {
        row("Background Color (hex):") {
            textField()
                .bindText(settings::backgroundColor)
                .comment("e.g., #FFFF00 (yellow), #FF000080 (semi-transparent red)")
        }
        row("Foreground Color (hex, optional):") {
            textField()
                .bindText(settings::foregroundColor)
        }
        row("Number of Blinks:") {
            spinner(1..10, 1)
                .bindIntValue(settings::blinkCount)
        }
        row("Blink Interval (ms):") {
            spinner(50..1000, 25)
                .bindIntValue(settings::blinkInterval)
        }
    }
}
