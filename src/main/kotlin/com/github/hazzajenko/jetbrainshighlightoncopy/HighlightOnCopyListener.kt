package com.github.hazzajenko.jetbrainshighlightoncopy

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.AnActionResult
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.util.TextRange
import com.github.hazzajenko.jetbrainshighlightoncopy.settings.HighlightOnCopySettings
import java.awt.Color
import java.awt.Font
import java.util.*
import kotlin.collections.ArrayList

@Service(Service.Level.PROJECT)
class HighlightOnCopyListener : AnActionListener {

    override fun afterActionPerformed(action: AnAction, event: AnActionEvent, result: AnActionResult) {
        // Check if this was a copy action
        if (action.javaClass.simpleName.contains("Copy") ||
            action.toString().contains("Copy") ||
            event.actionManager.getId(action) == "\$Copy") {

            val editor = event.getData(CommonDataKeys.EDITOR) ?: return
            val project = event.getData(CommonDataKeys.PROJECT) ?: return

            handleCopyAction(editor, project)
        }
    }

    private fun handleCopyAction(editor: Editor, project: Project) {
        val settings = HighlightOnCopySettings.getInstance()

        // Get selections to highlight (handles multi-cursor and empty selections)
        val selections = getSelections(editor)

        if (selections.isEmpty()) return

        // Apply highlighting with blinking effect
        highlightSelections(editor, selections, settings, project)
    }

    private fun getSelections(editor: Editor): List<TextRange> {
        val caretModel = editor.caretModel
        val document = editor.document
        val selections = mutableListOf<TextRange>()
        var lastSelectionLine = -1

        // Sort carets by line and character position
        val sortedCarets = caretModel.allCarets.sortedWith { a, b ->
            when {
                a.logicalPosition.line != b.logicalPosition.line ->
                    a.logicalPosition.line - b.logicalPosition.line
                else -> a.logicalPosition.column - b.logicalPosition.column
            }
        }

        for (caret in sortedCarets) {
            val currentLine = caret.logicalPosition.line

            // Skip empty selections on the same line (after the first selection)
            if (!caret.hasSelection() && lastSelectionLine == currentLine) {
                continue
            }

            lastSelectionLine = currentLine

            if (caret.hasSelection()) {
                // Use the actual selection
                selections.add(TextRange(caret.selectionStart, caret.selectionEnd))
            } else {
                // For empty selections, copy the entire line (like VSCode behavior)
                if (currentLine < document.lineCount) {
                    val lineStartOffset = document.getLineStartOffset(currentLine)
                    val lineEndOffset = document.getLineEndOffset(currentLine)
                    selections.add(TextRange(lineStartOffset, lineEndOffset))
                }
            }
        }

        return selections
    }

    private fun highlightSelections(
        editor: Editor,
        selections: List<TextRange>,
        settings: HighlightOnCopySettings,
        project: Project
    ) {
        val markupModel = editor.markupModel
        val highlighters = mutableListOf<RangeHighlighter>()

        val textAttributes = TextAttributes().apply {
            backgroundColor = parseColor(settings.backgroundColor)
            foregroundColor = if (settings.foregroundColor.isNotEmpty()) {
                parseColor(settings.foregroundColor)
            } else null
            effectType = null
            fontType = Font.BOLD
        }

        // Create blinking effect based on blink count
        if (selections.isNotEmpty()) {
            val timer = Timer()
            var isHighlighted = false
            var blinkCounter = 0
            val maxBlinks = settings.blinkCount * 2 // Each blink = on + off
            val blinkInterval = settings.blinkInterval.toLong()

            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    ApplicationManager.getApplication().invokeLater {
                        if (project.isDisposed) {
                            timer.cancel()
                            return@invokeLater
                        }

                        // 1. Toggle state and update highlighters
                        isHighlighted = !isHighlighted

                        // Always remove previous highlighters
                        highlighters.forEach { highlighter ->
                            try {
                                markupModel.removeHighlighter(highlighter)
                            } catch (e: Exception) {
                                // Highlighter might already be removed
                            }
                        }
                        highlighters.clear()

                        if (isHighlighted) {
                            // This is an "ON" tick. Clear selection and add colored highlighters.
                            editor.selectionModel.removeSelection()
                            for (selection in selections) {
                                try {
                                    val highlighter = markupModel.addRangeHighlighter(
                                        selection.startOffset,
                                        selection.endOffset,
                                        HighlighterLayer.SELECTION + 1,
                                        textAttributes,
                                        HighlighterTargetArea.EXACT_RANGE
                                    )
                                    highlighters.add(highlighter)
                                } catch (e: Exception) {
                                    continue
                                }
                            }
                        }
                        // On an "OFF" tick, we do nothing, as highlighters are already cleared.

                        // 2. Increment counter
                        blinkCounter++

                        // 3. Check if the animation is complete
                        if (blinkCounter >= maxBlinks) {
                            // Animation finished. The last state was "OFF".
                            // Highlighters are already gone.
                            timer.cancel()

                            // Restore the selection immediately.
                            restoreSelection(editor, selections)
                        }
                    }
                }
            }, 0, blinkInterval)
        }
    }

    private fun restoreSelection(editor: Editor, selections: List<TextRange>) {
        // Ensure this runs on the UI thread
        ApplicationManager.getApplication().invokeLater {
            editor.caretModel.removeSecondaryCarets()
            if (selections.isNotEmpty()) {
                // Restore the primary selection
                val firstSelection = selections.first()
                editor.selectionModel.setSelection(firstSelection.startOffset, firstSelection.endOffset)
                // Move the caret to the end of the selection, which is standard behavior
                editor.caretModel.moveToOffset(firstSelection.endOffset)

                // Restore any other selections as secondary carets
                selections.drop(1).forEach { range ->
                    val caret = editor.caretModel.addCaret(editor.offsetToLogicalPosition(range.endOffset), true)
                    caret?.setSelection(range.startOffset, range.endOffset)
                }
            }
        }
    }

    private fun parseColor(hexColor: String): Color {
        return try {
            if (hexColor.length == 9 && hexColor.startsWith("#")) {
                // 8-character hex with alpha: #RRGGBBAA
                val rgb = hexColor.substring(1, 7)
                val alpha = hexColor.substring(7, 9)
                val r = rgb.substring(0, 2).toInt(16)
                val g = rgb.substring(2, 4).toInt(16)
                val b = rgb.substring(4, 6).toInt(16)
                val a = alpha.toInt(16)
                Color(r, g, b, a)
            } else {
                // Standard 6-character hex or let Color.decode handle it
                Color.decode(hexColor)
            }
        } catch (e: Exception) {
            // Fallback to yellow if color parsing fails
            Color.YELLOW
        }
    }

    companion object {
        fun getInstance(project: Project): HighlightOnCopyListener {
            return project.getService(HighlightOnCopyListener::class.java)
        }
    }
}

// Project startup activity to register the listener
class HighlightOnCopyStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val actionManager = ActionManager.getInstance()
        val listener = HighlightOnCopyListener.getInstance(project)

        // Register the listener
        actionManager.addAnActionListener(listener, project)
    }
}