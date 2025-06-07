package com.github.hazzajenko.jetbrainshighlightoncopy.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class HighlightOnCopyConfigurable : Configurable {

    private var settingsComponent: HighlightOnCopySettingsComponent? = null

    override fun getDisplayName(): String = "Highlight on Copy"

    override fun createComponent(): JComponent? {
        settingsComponent = HighlightOnCopySettingsComponent()
        return settingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = HighlightOnCopySettings.getInstance()
        val component = settingsComponent!!

        return settings.backgroundColor != component.backgroundColorHex ||
                settings.foregroundColor != component.foregroundColorHex ||
                settings.timeout != component.timeout ||
                settings.blinkCount != component.blinkCount ||
                settings.blinkInterval != component.blinkInterval
    }

    override fun apply() {
        val settings = HighlightOnCopySettings.getInstance()
        val component = settingsComponent!!

        settings.backgroundColor = component.backgroundColorHex
        settings.foregroundColor = component.foregroundColorHex
        settings.timeout = component.timeout
        settings.blinkCount = component.blinkCount
        settings.blinkInterval = component.blinkInterval
    }

    override fun reset() {
        val settings = HighlightOnCopySettings.getInstance()
        val component = settingsComponent!!

        component.backgroundColorHex = settings.backgroundColor
        component.foregroundColorHex = settings.foregroundColor
        component.timeout = settings.timeout
        component.blinkCount = settings.blinkCount
        component.blinkInterval = settings.blinkInterval
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    private class HighlightOnCopySettingsComponent {
        val panel: JPanel
        private val backgroundColorField: JBTextField
        private val foregroundColorField: JBTextField
        private val timeoutSpinner: JSpinner
        private val blinkCountSpinner: JSpinner
        private val blinkIntervalSpinner: JSpinner

        var backgroundColorHex: String
            get() = backgroundColorField.text
            set(value) {
                backgroundColorField.text = value
            }

        var foregroundColorHex: String
            get() = foregroundColorField.text
            set(value) {
                foregroundColorField.text = value
            }

        var timeout: Int
            get() = timeoutSpinner.value as Int
            set(value) {
                timeoutSpinner.value = value
            }

        var blinkCount: Int
            get() = blinkCountSpinner.value as Int
            set(value) {
                blinkCountSpinner.value = value
            }

        var blinkInterval: Int
            get() = blinkIntervalSpinner.value as Int
            set(value) {
                blinkIntervalSpinner.value = value
            }

        init {
            backgroundColorField = JBTextField()
            foregroundColorField = JBTextField()
            timeoutSpinner = JSpinner(SpinnerNumberModel(1000, 100, 10000, 100))
            blinkCountSpinner = JSpinner(SpinnerNumberModel(3, 1, 10, 1))
            blinkIntervalSpinner = JSpinner(SpinnerNumberModel(150, 50, 1000, 25))

            // Set placeholder text to help users
            backgroundColorField.toolTipText = "Hex color code, e.g., #FFFF00 for yellow"
            foregroundColorField.toolTipText = "Hex color code, leave empty for no change, e.g., #000000 for black"

            panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("Background Color (hex):"), backgroundColorField, 1, false)
                .addLabeledComponent(JBLabel("Foreground Color (hex, optional):"), foregroundColorField, 1, false)
                .addLabeledComponent(JBLabel("Number of Blinks:"), blinkCountSpinner, 1, false)
                .addLabeledComponent(JBLabel("Blink Interval (ms):"), blinkIntervalSpinner, 1, false)
                .addLabeledComponent(JBLabel("Highlight Timeout (ms, legacy):"), timeoutSpinner, 1, false)
                .addComponent(JBLabel("Examples: #FFFF00 (yellow), #FF0000 (red), #00FF00 (green)"), 1)
                .addComponentFillVertically(JPanel(), 0)
                .panel
        }
    }
}