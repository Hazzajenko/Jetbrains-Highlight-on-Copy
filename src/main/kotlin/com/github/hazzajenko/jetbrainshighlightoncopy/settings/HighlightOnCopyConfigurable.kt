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
                settings.timeout != component.timeout
    }

    override fun apply() {
        val settings = HighlightOnCopySettings.getInstance()
        val component = settingsComponent!!

        settings.backgroundColor = component.backgroundColorHex
        settings.foregroundColor = component.foregroundColorHex
        settings.timeout = component.timeout
    }

    override fun reset() {
        val settings = HighlightOnCopySettings.getInstance()
        val component = settingsComponent!!

        component.backgroundColorHex = settings.backgroundColor
        component.foregroundColorHex = settings.foregroundColor
        component.timeout = settings.timeout
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    private class HighlightOnCopySettingsComponent {
        val panel: JPanel
        private val backgroundColorField: JBTextField
        private val foregroundColorField: JBTextField
        private val timeoutSpinner: JSpinner

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

        init {
            backgroundColorField = JBTextField()
            foregroundColorField = JBTextField()
            timeoutSpinner = JSpinner(SpinnerNumberModel(1000, 100, 10000, 100))

            // Set placeholder text to help users
            backgroundColorField.toolTipText = "Hex color code, e.g., #FFFF00 for yellow"
            foregroundColorField.toolTipText = "Hex color code, leave empty for no change, e.g., #000000 for black"

            panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("Background Color (hex):"), backgroundColorField, 1, false)
                .addLabeledComponent(JBLabel("Foreground Color (hex, optional):"), foregroundColorField, 1, false)
                .addLabeledComponent(JBLabel("Highlight Timeout (ms):"), timeoutSpinner, 1, false)
                .addComponent(JBLabel("Examples: #FFFF00 (yellow), #FF0000 (red), #00FF00 (green)"), 1)
                .addComponentFillVertically(JPanel(), 0)
                .panel
        }
    }
}