package dam_a51696.recipebuddy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for storing favorite meals locally
 */
@Entity(tableName = "favorite_meals")
data class FavoriteMealEntity(
    @PrimaryKey
    @ColumnInfo(name = "meal_id")
    val mealId: String,
    
    @ColumnInfo(name = "meal_name")
    val name: String,
    
    @ColumnInfo(name = "meal_image_url")
    val imageUrl: String,
    
    @ColumnInfo(name = "meal_category")
    val category: String?,
    
    @ColumnInfo(name = "meal_area")
    val area: String?,
    
    @ColumnInfo(name = "meal_instructions")
    val instructions: String,
    
    @ColumnInfo(name = "ingredients_json")
    val ingredientsJson: String, // Store as JSON string
    
    @ColumnInfo(name = "added_timestamp")
    val addedTimestamp: Long = System.currentTimeMillis()
)
