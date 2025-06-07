package com.github.hazzajenko.jetbrainshighlightoncopy

import com.intellij.ide.CopyPasteManagerEx
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.github.hazzajenko.jetbrainshighlightoncopy.settings.HighlightOnCopySettings
import java.awt.Color
import java.awt.Font
import java.awt.datatransfer.StringSelection
import java.util.*
import kotlin.collections.ArrayList

class HighlightOnCopyAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.getData(CommonDataKeys.PROJECT) ?: return

        val settings = HighlightOnCopySettings.getInstance()

        // Get selections to copy (handles multi-cursor and empty selections)
        val selections = getSelections(editor)

        if (selections.isEmpty()) return

        // Copy to clipboard (this will handle the actual copying)
        performCopy(editor, selections)

        // Apply highlighting
        highlightSelections(editor, selections, settings, project)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null
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

    private fun performCopy(editor: Editor, selections: List<TextRange>) {
        // Build the text to copy
        val document = editor.document
        val textToCopy = if (selections.size == 1) {
            // Single selection - copy as is
            document.getText(selections[0])
        } else {
            // Multiple selections - join with newlines
            selections.joinToString("\n") { document.getText(it) }
        }

        // Copy to clipboard using Transferable
        val copyPasteManager = CopyPasteManagerEx.getInstanceEx()
        copyPasteManager.setContents(StringSelection(textToCopy))
    }

    private fun highlightSelections(
        editor: Editor,
        selections: List<TextRange>,
        settings: HighlightOnCopySettings,
        project: Project
    ) {
        val markupModel = editor.markupModel
        val highlighters = mutableListOf<RangeHighlighter>()

        // Create text attributes for highlight with higher priority styling
        val textAttributes = TextAttributes().apply {
            backgroundColor = parseColor(settings.backgroundColor)
            foregroundColor = if (settings.foregroundColor.isNotEmpty()) {
                parseColor(settings.foregroundColor)
            } else null
            // Make it more visible by adding effects
            effectType = null
            fontType = Font.BOLD
        }

        // Create empty text attributes for "off" state
        val emptyTextAttributes = TextAttributes()

        // Add highlighters for each selection
        for (selection in selections) {
            try {
                val highlighter = markupModel.addRangeHighlighter(
                    selection.startOffset,
                    selection.endOffset,
                    HighlighterLayer.ADDITIONAL_SYNTAX + 1, // High priority layer
                    emptyTextAttributes, // Start with no highlight
                    HighlighterTargetArea.EXACT_RANGE
                )
                highlighters.add(highlighter)
            } catch (e: Exception) {
                // Handle any potential issues with invalid ranges
                continue
            }
        }

        // Create blinking effect
        if (highlighters.isNotEmpty()) {
            val timer = Timer()
            var isHighlighted = false
            val blinkInterval = 100L // 100ms blink interval
            val totalDuration = settings.timeout.toLong()
            var elapsedTime = 0L

            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    ApplicationManager.getApplication().invokeLater {
                        if (!project.isDisposed && elapsedTime < totalDuration) {
                            isHighlighted = !isHighlighted
                            
                            // Remove current highlighters
                            highlighters.forEach { highlighter ->
                                try {
                                    markupModel.removeHighlighter(highlighter)
                                } catch (e: Exception) {
                                    // Highlighter might already be removed
                                }
                            }
                            highlighters.clear()

                            // Re-add highlighters with new state if highlighted
                            if (isHighlighted) {
                                // Clear any existing selection to make our highlight visible
                                editor.selectionModel.removeSelection()
                                
                                for (selection in selections) {
                                    try {
                                        val highlighter = markupModel.addRangeHighlighter(
                                            selection.startOffset,
                                            selection.endOffset,
                                            HighlighterLayer.SELECTION + 1, // Higher than selection layer
                                            textAttributes,
                                            HighlighterTargetArea.EXACT_RANGE
                                        )
                                        highlighters.add(highlighter)
                                    } catch (e: Exception) {
                                        // Handle any potential issues with invalid ranges
                                        continue
                                    }
                                }
                            }
                            
                            elapsedTime += blinkInterval
                        } else {
                            // Final cleanup
                            highlighters.forEach { highlighter ->
                                try {
                                    markupModel.removeHighlighter(highlighter)
                                } catch (e: Exception) {
                                    // Highlighter might already be removed
                                }
                            }
                            timer.cancel()
                        }
                    }
                }
            }, 0, blinkInterval)
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
}