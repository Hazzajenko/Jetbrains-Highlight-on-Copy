package com.github.hazzajenko.jetbrainshighlightoncopy

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.AnActionResult
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.application.EDT
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.github.hazzajenko.jetbrainshighlightoncopy.settings.HighlightOnCopySettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.intellij.openapi.diagnostic.thisLogger
import java.awt.Color
import java.awt.Font
import java.util.WeakHashMap

class HighlightOnCopyListener(
    private val project: Project,
    private val scope: CoroutineScope
) : AnActionListener {

    // Data class to hold selection info, including whether it was a real selection
    private data class HighlightInfo(val range: TextRange, val wasOriginalSelection: Boolean)

    // Use a WeakHashMap to store the state before the action runs.
    // This prevents memory leaks if an editor is closed.
    private val preActionSelections = WeakHashMap<Editor, List<HighlightInfo>>()

    /**
     * Captures the editor's selection state *before* the copy action modifies it.
     */
    override fun beforeActionPerformed(action: AnAction, event: AnActionEvent) {
        if (isCopyAction(action, event)) {
            val editor = event.getData(CommonDataKeys.EDITOR) ?: return
            // Get the selection state now and store it.
            val selections = getSelections(editor)
            if (selections.isNotEmpty()) {
                preActionSelections[editor] = selections
            }
        }
    }

    /**
     * After the action, retrieves the pre-captured state and starts the highlight.
     */
    override fun afterActionPerformed(action: AnAction, event: AnActionEvent, result: AnActionResult) {
        if (isCopyAction(action, event)) {
            val editor = event.getData(CommonDataKeys.EDITOR) ?: return

            // Retrieve and remove the state we captured before the action.
            val selections = preActionSelections.remove(editor) ?: return

            // Now, trigger the highlight using the original, unmodified selection state.
            handleCopyAction(editor, selections)
        }
    }

    private fun isCopyAction(action: AnAction, event: AnActionEvent): Boolean {
        val actionId = event.actionManager.getId(action)
        return actionId == "\$Copy" ||
                action.javaClass.simpleName.contains("Copy", ignoreCase = true) ||
                action.toString().contains("Copy", ignoreCase = true)
    }

    private fun handleCopyAction(editor: Editor, selections: List<HighlightInfo>) {
        val settings = HighlightOnCopySettings.getInstance()
        if (selections.isEmpty()) return

        // Apply highlighting with blinking effect
        highlightSelections(editor, selections, settings)
    }

    private fun getSelections(editor: Editor): List<HighlightInfo> {
        val caretModel = editor.caretModel
        val document = editor.document
        val selections = mutableListOf<HighlightInfo>()
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
                selections.add(HighlightInfo(TextRange(caret.selectionStart, caret.selectionEnd), true))
            } else {
                // For empty selections, copy the entire line (like VSCode behavior)
                if (currentLine < document.lineCount) {
                    val lineStartOffset = document.getLineStartOffset(currentLine)
                    val lineEndOffset = document.getLineEndOffset(currentLine)
                    selections.add(HighlightInfo(TextRange(lineStartOffset, lineEndOffset), false))
                }
            }
        }

        return selections
    }

    private fun highlightSelections(
        editor: Editor,
        selections: List<HighlightInfo>,
        settings: HighlightOnCopySettings
    ) {
        if (selections.isEmpty()) return

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

        val maxBlinks = settings.blinkCount * 2 // Each blink = on + off
        val blinkInterval = settings.blinkInterval.toLong()

        scope.launch {
            var isHighlighted = false
            repeat(maxBlinks) {
                if (project.isDisposed) return@launch

                isHighlighted = !isHighlighted

                withContext(Dispatchers.EDT) {
                    if (editor.isDisposed) return@withContext

                    // Remove existing highlighters
                    highlighters.forEach { highlighter ->
                        try {
                            markupModel.removeHighlighter(highlighter)
                        } catch (e: Exception) {
                            thisLogger().debug("Highlighter already removed", e)
                        }
                    }
                    highlighters.clear()

                    // Add new highlighters if in highlighted state
                    if (isHighlighted) {
                        for (selectionInfo in selections) {
                            try {
                                val highlighter = markupModel.addRangeHighlighter(
                                    selectionInfo.range.startOffset,
                                    selectionInfo.range.endOffset,
                                    HighlighterLayer.SELECTION + 1,
                                    textAttributes,
                                    HighlighterTargetArea.EXACT_RANGE
                                )
                                highlighters.add(highlighter)
                            } catch (e: Exception) {
                                thisLogger().debug("Failed to add range highlighter", e)
                                continue
                            }
                        }
                    }
                }

                delay(blinkInterval)
            }

            // Restore selection after blinking completes
            withContext(Dispatchers.EDT) {
                if (!editor.isDisposed) {
                    restoreSelection(editor, selections)
                }
            }
        }
    }

    private fun restoreSelection(editor: Editor, highlightInfos: List<HighlightInfo>) {
        if (editor.isDisposed) return

        editor.caretModel.removeSecondaryCarets()
        if (highlightInfos.isNotEmpty()) {
            val firstInfo = highlightInfos.first()
            if (firstInfo.wasOriginalSelection) {
                // It was a real selection, so restore it.
                editor.selectionModel.setSelection(firstInfo.range.startOffset, firstInfo.range.endOffset)
                editor.caretModel.moveToOffset(firstInfo.range.endOffset)
            } else {
                // It was a line copy. The default action may have moved the caret.
                // We must NOT create a selection. Just ensure the primary caret has none.
                editor.caretModel.primaryCaret.removeSelection()
            }

            // Restore any other selections as secondary carets
            highlightInfos.drop(1).forEach { info ->
                // Place the caret at the end of the range, which is standard.
                val newCaret = editor.caretModel.addCaret(editor.offsetToLogicalPosition(info.range.endOffset), true)
                if (info.wasOriginalSelection) {
                    newCaret?.setSelection(info.range.startOffset, info.range.endOffset)
                }
            }
        }
    }

    companion object {
        fun parseColor(hexColor: String): Color {
            return try {
                if (hexColor.length == 9 && hexColor.startsWith("#")) {
                    val rgb = hexColor.substring(1, 7)
                    val alpha = hexColor.substring(7, 9)
                    val r = rgb.substring(0, 2).toInt(16)
                    val g = rgb.substring(2, 4).toInt(16)
                    val b = rgb.substring(4, 6).toInt(16)
                    val a = alpha.toInt(16)
                    Color(r, g, b, a)
                } else {
                    Color.decode(hexColor)
                }
            } catch (e: Exception) {
                Color.YELLOW
            }
        }
    }
}
