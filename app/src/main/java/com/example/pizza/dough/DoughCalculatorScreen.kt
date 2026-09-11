package com.example.pizza.dough

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizza.R
import com.example.pizza.ui.theme.PizzaTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoughCalculatorContent(
    recipe: DoughRecipe,
    onRecipeChange: (DoughRecipe) -> Unit,
    onSaveRecipe: (name: String) -> Unit,
    onOpenSavedRecipes: () -> Unit,
    onOpenFullRecipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSaveDialog by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Dough calculator") },
                actions = {
                    IconButton(onClick = { showSaveDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_bookmark),
                            contentDescription = "Save recipe"
                        )
                    }
                    IconButton(onClick = onOpenSavedRecipes) {
                        Icon(
                            painter = painterResource(R.drawable.ic_list),
                            contentDescription = "Saved recipes"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onOpenFullRecipe) { Text("Full recipe") }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PizzaSizeCard(recipe = recipe, onRecipeChange = onRecipeChange)
            BakerPercentagesCard(recipe = recipe, onRecipeChange = onRecipeChange)
            DoughResultCard(recipe = recipe)
            // Keeps the last card clear of the floating button.
            Spacer(modifier = Modifier.height(72.dp))
        }
        if (showSaveDialog) {
            SaveRecipeDialog(
                onSave = { name ->
                    showSaveDialog = false
                    onSaveRecipe(name)
                    scope.launch { snackbarHostState.showSnackbar("Saved \"$name\"") }
                },
                onDismiss = { showSaveDialog = false }
            )
        }
    }
}

@Composable
private fun SaveRecipeDialog(
    onSave: (name: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onSave(name.trim())
            }, enabled = name.isNotBlank()) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Save recipe") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                placeholder = { Text("e.g. Friday pizza night") },
                singleLine = true
            )
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun DoughCalculatorContentPreview() {
    PizzaTheme {
        DoughCalculatorContent(
            recipe = DefaultDoughRecipe,
            onRecipeChange = {},
            onSaveRecipe = {},
            onOpenSavedRecipes = {},
            onOpenFullRecipe = {}
        )
    }
}
