package com.example.pizza.dough

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizza.R
import com.example.pizza.ui.theme.PizzaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedRecipesScreen(
    savedRecipes: List<SavedRecipe>,
    onOpenRecipe: (SavedRecipe) -> Unit,
    onEditRecipe: (SavedRecipe) -> Unit,
    onDeleteRecipe: (SavedRecipe) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var recipeToDelete by remember { mutableStateOf<SavedRecipe?>(null) }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Saved recipes") },
                navigationIcon = { BackButton(onClick = onBack) }
            )
        }
    ) { innerPadding ->
        if (savedRecipes.isEmpty()) {
            EmptySavedRecipes(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedRecipes, key = { it.id }) { savedRecipe ->
                    SavedRecipeItem(
                        savedRecipe = savedRecipe,
                        onOpen = { onOpenRecipe(savedRecipe) },
                        onEdit = { onEditRecipe(savedRecipe) },
                        onDelete = { recipeToDelete = savedRecipe },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
        recipeToDelete?.let { savedRecipe ->
            DeleteRecipeDialog(
                recipeName = savedRecipe.name,
                onConfirm = {
                    onDeleteRecipe(savedRecipe)
                    recipeToDelete = null
                },
                onDismiss = { recipeToDelete = null }
            )
        }
    }
}

@Composable
private fun SavedRecipeItem(
    savedRecipe: SavedRecipe,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(onClick = onOpen, modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = savedRecipe.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = savedRecipe.recipe.summary(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = "Edit in calculator"
                )
            }
            IconButton(onClick = onDelete) {
                Icon(painter = painterResource(R.drawable.ic_delete), contentDescription = "Delete")
            }
        }
    }
}

@Composable
private fun EmptySavedRecipes(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No saved recipes yet.\nTap the bookmark in the calculator to save one.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DeleteRecipeDialog(
    recipeName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onConfirm) { Text("Delete") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Delete \"$recipeName\"?") },
        modifier = modifier
    )
}

private fun DoughRecipe.summary() =
    "$portionCount × $portionWeightGrams g · ${hydrationPercent.formatDecimals(1)}% hydration"

@Preview(showBackground = true)
@Composable
private fun SavedRecipesScreenPreview() {
    PizzaTheme {
        SavedRecipesScreen(
            savedRecipes = listOf(
                SavedRecipe(id = "1", name = "Friday pizza night", recipe = DefaultDoughRecipe)
            ),
            onOpenRecipe = {},
            onEditRecipe = {},
            onDeleteRecipe = {},
            onBack = {}
        )
    }
}
