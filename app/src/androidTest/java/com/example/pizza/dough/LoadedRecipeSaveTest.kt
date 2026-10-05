package com.example.pizza.dough

import android.view.KeyEvent
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadedRecipeSaveTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var updateCount = 0
    private val savedNames = mutableListOf<String>()

    private fun tapSave(loadedRecipeName: String?) {
        composeRule.setContent {
            DoughCalculatorContent(
                recipe = DefaultDoughRecipe,
                loadedRecipeName = loadedRecipeName,
                resetMessagePending = false,
                onRecipeChange = {},
                onSaveRecipe = { savedNames += it },
                onUpdateLoadedRecipe = { updateCount++ },
                onOpenSavedRecipes = {},
                onOpenFullRecipe = {},
                onDismissResetMessage = {}
            )
        }
        composeRule.onNodeWithContentDescription("Save recipe").performClick()
    }

    @Test
    fun loadedRecipeOffersUpdateAndSaveAsNew() {
        tapSave(loadedRecipeName = "Friday night")
        composeRule.onNodeWithText("Update \"Friday night\"").assertExists()
        composeRule.onNodeWithText("Save as new…").assertExists()
    }

    @Test
    fun updateUpdatesOnceAndConfirms() {
        tapSave(loadedRecipeName = "Friday night")
        composeRule.onNodeWithText("Update \"Friday night\"").performClick()
        assertEquals(1, updateCount)
        assertEquals(emptyList<String>(), savedNames)
        composeRule.onNodeWithText("Updated \"Friday night\"").assertExists()
    }

    @Test
    fun saveAsNewAsksForAName() {
        tapSave(loadedRecipeName = "Friday night")
        composeRule.onNodeWithText("Save as new…").performClick()
        composeRule.onNodeWithText("Name").performTextInput("Wetter Friday")
        composeRule.onNodeWithText("Save").performClick()
        assertEquals(listOf("Wetter Friday"), savedNames)
        assertEquals(0, updateCount)
    }

    @Test
    fun cancelSavesNothing() {
        tapSave(loadedRecipeName = "Friday night")
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNodeWithText("Update \"Friday night\"").assertDoesNotExist()
        assertSavedNothing()
    }

    @Test
    fun backSavesNothing() {
        tapSave(loadedRecipeName = "Friday night")
        // A real Back key goes to the focused window, the dialog. Espresso's pressBack waits for the
        // calculator's window to get focus, which it never does while a dialog it opened is shown.
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Update \"Friday night\"").assertDoesNotExist()
        assertSavedNothing()
    }

    @Test
    fun withoutALoadedRecipeTheNameDialogOpensDirectly() {
        tapSave(loadedRecipeName = null)
        composeRule.onNodeWithText("Name").assertExists()
        composeRule.onNodeWithText("Save as new…").assertDoesNotExist()
    }

    private fun assertSavedNothing() {
        assertEquals(0, updateCount)
        assertEquals(emptyList<String>(), savedNames)
    }
}
