package com.github.hazzajenko.jetbrainshighlightoncopy.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@Service
@State(
    name = "HighlightOnCopySettings",
    storages = [Storage("HighlightOnCopySettings.xml")]
)
class HighlightOnCopySettings : PersistentStateComponent<HighlightOnCopySettings> {

    var backgroundColor: String = "#E66159"  // Light red
    var foregroundColor: String = ""         // Empty means no foreground color change
    var timeout: Int = 1000                  // 1 second timeout (default) - kept for backwards compatibility
    var blinkCount: Int = 1                  // Number of blinks (default)
    var blinkInterval: Int = 150             // Blink interval in milliseconds (default)

    override fun getState(): HighlightOnCopySettings = this

    override fun loadState(state: HighlightOnCopySettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): HighlightOnCopySettings {
            return ApplicationManager.getApplication().getService(HighlightOnCopySettings::class.java)
        }
    }
}