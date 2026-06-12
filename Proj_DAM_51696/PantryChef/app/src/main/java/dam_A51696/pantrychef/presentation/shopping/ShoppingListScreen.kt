package dam_A51696.pantrychef.presentation.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.GrayText
import dam_A51696.pantrychef.presentation.theme.TagGreen
import dam_A51696.pantrychef.presentation.theme.White
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import dam_A51696.pantrychef.domain.model.ShoppingItem

@Composable
fun ShoppingListScreen(
    viewModel: ShoppingViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()
    val toBuy = items.filter { !it.isBought }
    val recentlyBought = items.filter { it.isBought }

    var newItemName by remember { mutableStateOf("") }

    Scaffold(
        containerColor = CreamBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            )
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Shopping List",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "${toBuy.size} items needed",
                            fontSize = 14.sp,
                            color = GrayText
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(40.dp)
                                .background(White, CircleShape)
                                .border(1.dp, Color.LightGray, CircleShape)
                        ) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = GrayText)
                        }
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(40.dp)
                                .background(White, CircleShape)
                                .border(1.dp, Color.LightGray, CircleShape)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Check all", tint = ForestGreen)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Add custom item...", color = Color.Black) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.border(1.dp, ForestGreen, CircleShape).padding(2.dp).size(16.dp).clickable {
                                viewModel.addItem(newItemName, "Manual entry")
                                newItemName = ""
                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.addItem(newItemName, "Manual entry")
                            newItemName = ""
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "TO BUY",
                    color = GrayText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            items(toBuy.size) { index ->
                ShoppingItemCard(
                    item = toBuy[index],
                    onToggle = { viewModel.toggleItem(toBuy[index]) },
                    onDelete = { viewModel.deleteItem(toBuy[index]) }
                )
            }

            if (recentlyBought.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        text = "RECENTLY BOUGHT",
                        color = GrayText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                items(recentlyBought.size) { index ->
                    BoughtItemCard(
                        item = recentlyBought[index],
                        onToggle = { viewModel.toggleItem(recentlyBought[index]) },
                        onDelete = { viewModel.deleteItem(recentlyBought[index]) }
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingItemCard(item: ShoppingItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(2.dp, TagGreen, CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(text = item.details, color = GrayText, fontSize = 12.sp)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.LightGray, modifier = Modifier.border(1.dp, Color.LightGray, CircleShape).padding(4.dp).size(12.dp))
            }
        }
    }
}

@Composable
fun BoughtItemCard(item: ShoppingItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamBackground), // Same as background for a blended look
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(TagGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GrayText, textDecoration = TextDecoration.LineThrough)
                Text(text = item.details, color = Color.LightGray, fontSize = 12.sp)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.LightGray, modifier = Modifier.border(1.dp, Color.LightGray, CircleShape).padding(4.dp).size(12.dp))
            }
        }
    }
}
