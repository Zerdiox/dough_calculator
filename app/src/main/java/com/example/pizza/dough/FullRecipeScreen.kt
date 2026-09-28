package com.example.pizza.dough

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizza.R
import com.example.pizza.ui.theme.PizzaTheme

/**
 * The recipe to follow while making dough. Portions and portion weight can be changed here
 * to re-calculate, without changing the saved recipe. The screen stays on while it's shown.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullRecipeScreen(
    title: String,
    initialRecipe: DoughRecipe,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var portionCount by rememberSaveable { mutableIntStateOf(initialRecipe.portionCount) }
    var portionWeightGrams by rememberSaveable {
        mutableIntStateOf(initialRecipe.portionWeightGrams)
    }
    val recipe = initialRecipe.copy(
        portionCount = portionCount,
        portionWeightGrams = portionWeightGrams
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier
            .keepScreenOn()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { BackButton(onClick = onBack) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PortionsCard(
                recipe = recipe,
                onRecipeChange = {
                    portionCount = it.portionCount
                    portionWeightGrams = it.portionWeightGrams
                }
            )
            DoughResultCard(recipe = recipe)
            Text(
                text = "The screen stays on while this recipe is open.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
internal fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(painter = painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
    }
}

@Preview(showBackground = true)
@Composable
private fun FullRecipeScreenPreview() {
    PizzaTheme {
        FullRecipeScreen(
            title = "Friday pizza night",
            initialRecipe = DefaultDoughRecipe,
            onBack = {}
        )
    }
}
