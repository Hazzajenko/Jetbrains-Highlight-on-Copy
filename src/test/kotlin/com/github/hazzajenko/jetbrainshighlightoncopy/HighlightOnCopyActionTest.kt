package com.github.hazzajenko.jetbrainshighlightoncopy

import com.intellij.ide.CopyPasteManagerEx
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.TextRange
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

class HighlightOnCopyActionTest : BasePlatformTestCase() {

    private lateinit var action: HighlightOnCopyAction

    override fun setUp() {
        super.setUp()
        action = HighlightOnCopyAction()
    }

    fun testCopyAndHighlightSimpleSelection() {
        // Create a test file with content
        val testContent = "Test text to be copied"
        val psiFile = myFixture.configureByText("test.txt", testContent)

        // Select all text
        val editor = myFixture.editor
        editor.selectionModel.setSelection(0, testContent.length)

        // Create action event
        val dataContext = SimpleDataContext.builder()
            .add(CommonDataKeys.EDITOR, editor)
            .add(CommonDataKeys.PROJECT, project)
            .build()
        val actionEvent = AnActionEvent.createFromDataContext("test", null, dataContext)

        // Execute the action
        action.actionPerformed(actionEvent)

        // Verify highlighting was applied (check that highlighters exist)
        val markupModel = editor.markupModel
        val highlighters = markupModel.allHighlighters
        assertTrue("Highlighters should be added", highlighters.isNotEmpty())
    }

    fun testActionUpdateWithNoEditor() {
        // Test that action is disabled when no editor is available
        val dataContext = SimpleDataContext.builder().build()
        val actionEvent = AnActionEvent.createFromDataContext("test", null, dataContext)

        action.update(actionEvent)

        assertFalse("Action should be disabled when no editor is available",
            actionEvent.presentation.isEnabledAndVisible)
    }

    fun testActionUpdateWithEditor() {
        // Test that action is enabled when editor is available
        val psiFile = myFixture.configureByText("test.txt", "Test content")
        val editor = myFixture.editor

        val dataContext = SimpleDataContext.builder()
            .add(CommonDataKeys.EDITOR, editor)
            .add(CommonDataKeys.PROJECT, project)
            .build()
        val actionEvent = AnActionEvent.createFromDataContext("test", null, dataContext)

        action.update(actionEvent)

        assertTrue("Action should be enabled when editor is available",
            actionEvent.presentation.isEnabledAndVisible)
    }
}