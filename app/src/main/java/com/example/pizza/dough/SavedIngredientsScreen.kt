package com.example.pizza.dough

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizza.R
import com.example.pizza.ui.theme.PizzaTheme
import kotlinx.coroutines.launch

/** Lists the saved ingredients, in the order given, each with a delete button. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedIngredientsScreen(
    savedIngredients: List<String>,
    onDeleteIngredient: (String) -> Unit,
    onRestoreIngredient: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    // Tied to this screen, so leaving it ends the message and the chance to undo.
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Saved ingredients") },
                navigationIcon = { BackButton(onClick = onBack) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (savedIngredients.isEmpty()) {
            EmptySavedIngredients(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(savedIngredients, key = { it }) { name ->
                SavedIngredientItem(
                    name = name,
                    onDelete = {
                        onDeleteIngredient(name)
                        scope.launch {
                            if (snackbarHostState.showUndoMessage("Deleted \"$name\"")) {
                                onRestoreIngredient(name)
                            }
                        }
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
private fun SavedIngredientItem(name: String, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = { Text(name) },
        modifier = modifier,
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Delete $name"
                )
            }
        }
    )
}

@Composable
private fun EmptySavedIngredients(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No saved ingredients.\nTick an ingredient when saving a recipe to add it here.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SavedIngredientsScreenPreview() {
    PizzaTheme {
        SavedIngredientsScreen(
            savedIngredients = listOf("Olive oil", "Salt", "Yeast"),
            onDeleteIngredient = {},
            onRestoreIngredient = {},
            onBack = {}
        )
    }
}
