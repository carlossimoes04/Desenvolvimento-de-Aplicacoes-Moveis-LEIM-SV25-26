package dam_A51696.pantrychef.presentation.pantry

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dam_A51696.pantrychef.domain.model.Ingredient
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.GrayText
import dam_A51696.pantrychef.presentation.theme.LightForestGreen
import dam_A51696.pantrychef.presentation.theme.LightOrange
import dam_A51696.pantrychef.presentation.theme.PrimaryOrange
import dam_A51696.pantrychef.presentation.theme.TagGreen
import dam_A51696.pantrychef.presentation.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(viewModel: PantryViewModel = hiltViewModel()) {
    val ingredients by viewModel.ingredients.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var ingredientToEdit by remember { mutableStateOf<Ingredient?>(null) }
    val expiringSoon = ingredients.filter { it.expirationDate < System.currentTimeMillis() + 4 * 24 * 60 * 60 * 1000L } // < 4 days
    val goodToGo = ingredients.filter { !expiringSoon.contains(it) }

    Scaffold(
        containerColor = CreamBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreen,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Ingredient")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Pantry",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "${expiringSoon.size} items expiring soon",
                            fontSize = 14.sp,
                            color = GrayText
                        )
                    }
                    // Profile Icon placeholder
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Me", color = White)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                // Search Bar
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search Ingredients...", color = GrayText) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GrayText) },
                    shape = RoundedCornerShape(24.dp),
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = ForestGreen
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (expiringSoon.isNotEmpty()) {
                item {
                    SectionHeader(title = "EXPIRING SOON", icon = Icons.Default.Warning, color = PrimaryOrange)
                }
                items(expiringSoon) { ingredient ->
                    IngredientCard(
                        ingredient = ingredient,
                        tagText = formatExpiringDate(ingredient.expirationDate),
                        tagBgColor = LightOrange,
                        tagTextColor = PrimaryOrange,
                        onEdit = { ingredientToEdit = ingredient },
                        onDelete = { viewModel.deleteIngredient(ingredient) }
                    )
                }
            }

            if (goodToGo.isNotEmpty()) {
                item {
                    SectionHeader(title = "GOOD TO GO", icon = Icons.Default.CheckCircle, color = ForestGreen)
                }
                items(goodToGo) { ingredient ->
                    IngredientCard(
                        ingredient = ingredient,
                        tagText = formatExpiringDate(ingredient.expirationDate),
                        tagBgColor = LightForestGreen,
                        tagTextColor = ForestGreen,
                        onEdit = { ingredientToEdit = ingredient },
                        onDelete = { viewModel.deleteIngredient(ingredient) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddIngredientDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, qty, unit, date ->
                viewModel.addIngredient(name, qty, unit, date)
                showAddDialog = false
            }
        )
    }

    ingredientToEdit?.let { ingredient ->
        EditIngredientDialog(
            ingredient = ingredient,
            onDismiss = { ingredientToEdit = null },
            onUpdate = { name, qty, unit, date ->
                val updated = ingredient.copy(
                    name = name,
                    quantity = qty,
                    unit = unit,
                    expirationDate = date
                )
                viewModel.updateIngredient(updated)
                ingredientToEdit = null
            }
        )
    }
}

@Composable
fun CategoryChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) ForestGreen else White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (isSelected) White else Color.Black,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, color = GrayText, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, letterSpacing = 1.sp)
    }
}

@Composable
fun IngredientCard(
    ingredient: Ingredient,
    tagText: String,
    tagBgColor: Color,
    tagTextColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CreamBackground),
                    contentAlignment = Alignment.Center
                ) {
                    // Placeholder icon
                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(tagTextColor))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = ingredient.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text(text = "${ingredient.quantity} ${ingredient.unit}", color = GrayText, fontSize = 14.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = tagBgColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = tagText,
                        color = tagTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Icon(
                        Icons.Default.Edit, 
                        contentDescription = "Edit", 
                        tint = GrayText,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onEdit() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = "Delete", 
                        tint = Color.Red,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onDelete() }
                    )
                }
            }
        }
    }
}

fun formatExpiringDate(timestamp: Long): String {
    val diff = timestamp - System.currentTimeMillis()
    val days = (diff / (1000 * 60 * 60 * 24)).toInt()
    return when {
        days < 0 -> "Expired"
        days == 0 -> "Expires today"
        days == 1 -> "Expires tomorrow"
        days < 30 -> "In $days days"
        else -> "In ${days / 30} months"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientDialog(onDismiss: () -> Unit, onAdd: (String, Double, String, Long) -> Unit) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var dateStr by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Ingredient", color = ForestGreen, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Name") }, modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity, onValueChange = { quantity = it },
                        label = { Text("Quantity") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = unit, onValueChange = { unit = it },
                        label = { Text("Unit (e.g. L, g)") }, modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = dateStr, 
                    onValueChange = { 
                        dateStr = it
                        dateError = false 
                    },
                    label = { Text("Expiration (DD/MM/YYYY, MM/YYYY, YYYY)") }, 
                    modifier = Modifier.fillMaxWidth(),
                    isError = dateError
                )
                if (dateError) {
                    Text("Invalid date format", color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantity.toDoubleOrNull() ?: 0.0
                    
                    var expirationMs: Long? = null
                    val formats = listOf(
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                        SimpleDateFormat("MM/yyyy", Locale.getDefault()),
                        SimpleDateFormat("yyyy", Locale.getDefault())
                    )
                    
                    for (format in formats) {
                        try {
                            format.isLenient = false
                            val parsedDate = format.parse(dateStr)
                            if (parsedDate != null) {
                                expirationMs = parsedDate.time
                                break
                            }
                        } catch (e: Exception) {
                            // try next format
                        }
                    }

                    if (expirationMs == null) {
                        dateError = true
                        return@Button
                    }

                    if (name.isNotBlank() && quantity.isNotBlank() && unit.isNotBlank()) {
                        onAdd(name, qty, unit, expirationMs)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                Text("Cancel", color = Color.Black)
            }
        },
        containerColor = White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIngredientDialog(
    ingredient: Ingredient,
    onDismiss: () -> Unit, 
    onUpdate: (String, Double, String, Long) -> Unit
) {
    var name by remember { mutableStateOf(ingredient.name) }
    var quantity by remember { mutableStateOf(if (ingredient.quantity % 1.0 == 0.0) ingredient.quantity.toInt().toString() else ingredient.quantity.toString()) }
    var unit by remember { mutableStateOf(ingredient.unit) }
    
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var dateStr by remember { mutableStateOf(format.format(Date(ingredient.expirationDate))) }
    var dateError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Ingredient", color = ForestGreen, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Name") }, modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity, onValueChange = { quantity = it },
                        label = { Text("Quantity") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = unit, onValueChange = { unit = it },
                        label = { Text("Unit (e.g. L, g)") }, modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = dateStr, 
                    onValueChange = { 
                        dateStr = it
                        dateError = false 
                    },
                    label = { Text("Expiration (DD/MM/YYYY, MM/YYYY, YYYY)") }, 
                    modifier = Modifier.fillMaxWidth(),
                    isError = dateError
                )
                if (dateError) {
                    Text("Invalid date format", color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantity.toDoubleOrNull() ?: 0.0
                    
                    var expirationMs: Long? = null
                    val formats = listOf(
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                        SimpleDateFormat("MM/yyyy", Locale.getDefault()),
                        SimpleDateFormat("yyyy", Locale.getDefault())
                    )
                    
                    for (f in formats) {
                        try {
                            f.isLenient = false
                            val parsedDate = f.parse(dateStr)
                            if (parsedDate != null) {
                                expirationMs = parsedDate.time
                                break
                            }
                        } catch (e: Exception) {
                            // try next
                        }
                    }

                    if (expirationMs == null) {
                        dateError = true
                        return@Button
                    }

                    if (name.isNotBlank() && quantity.isNotBlank() && unit.isNotBlank()) {
                        onUpdate(name, qty, unit, expirationMs)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                Text("Cancel", color = Color.Black)
            }
        },
        containerColor = White
    )
}
