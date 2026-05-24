package dam_A51696.pantrychef.presentation.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.GrayText
import dam_A51696.pantrychef.presentation.theme.LightForestGreen
import dam_A51696.pantrychef.presentation.theme.LightOrange
import dam_A51696.pantrychef.presentation.theme.PrimaryOrange
import dam_A51696.pantrychef.presentation.theme.White
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import dam_A51696.pantrychef.domain.model.RecipeDetail

@Composable
fun RecipeDetailScreen(
    recipeId: String?,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    when (state) {
        is RecipeDetailState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ForestGreen)
            }
        }
        is RecipeDetailState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = (state as RecipeDetailState.Error).message, color = Color.Red)
            }
        }
        is RecipeDetailState.Success -> {
            val recipe = (state as RecipeDetailState.Success).recipe
            RecipeDetailContent(
                recipe = recipe,
                onAddMissing = { viewModel.addMissingIngredients() }
            )
        }
    }
}

@Composable
fun RecipeDetailContent(recipe: RecipeDetail, onAddMissing: () -> Unit) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(CreamBackground)) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            // Top Image Header
            Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                AsyncImage(
                    model = recipe.strMealThumb,
                    contentDescription = "Recipe Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Top bar buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = White)
                        }
                        IconButton(
                            onClick = { },
                            modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = "Favorite", tint = PrimaryOrange)
                        }
                    }
                }
            }

            // Content Section
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 280.dp), // overlaps the image slightly
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = CreamBackground
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Drag handle
                    Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color.LightGray, RoundedCornerShape(2.dp)).align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(color = LightForestGreen, shape = RoundedCornerShape(12.dp)) {
                            Text("QUICK & EASY", color = ForestGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                        Surface(color = LightOrange, shape = RoundedCornerShape(12.dp)) {
                            Text("SUSTAINABILITY PICK", color = PrimaryOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Title
                    Text(
                        text = recipe.strMeal,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        lineHeight = 34.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Description
                    Text(
                        text = recipe.strInstructions,
                        color = GrayText,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    androidx.compose.material3.Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Info Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        InfoColumn(Icons.Default.List, "20 Min", "PREP TIME")
                        InfoColumn(Icons.Default.Person, "2 Pers", "SERVINGS")
                        InfoColumn(Icons.Default.Favorite, "450 kcal", "PER SERVE") // Placeholder icon for fire
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    androidx.compose.material3.Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Add to list button
                    Button(
                        onClick = onAddMissing,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add missing items to list", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Ingredients List
                    recipe.ingredients.forEach { (name, measure) ->
                        IngredientItemRow(name = name, amount = measure, inPantry = false) // Note: The usecase adds missing items, but here we don't know real-time pantry status unless we observe it. For simplicity, we just list them.
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun InfoColumn(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = GrayText, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
        Text(text = label, color = GrayText, fontSize = 10.sp)
    }
}

@Composable
fun IngredientItemRow(name: String, amount: String, inPantry: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(if (inPantry) LightForestGreen else CreamBackground),
            contentAlignment = Alignment.Center
        ) {
             if (inPantry) Icon(Icons.Default.List, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
            if (inPantry) {
                Text(text = "IN PANTRY", color = ForestGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(text = amount, color = GrayText, fontSize = 14.sp)
    }
}
