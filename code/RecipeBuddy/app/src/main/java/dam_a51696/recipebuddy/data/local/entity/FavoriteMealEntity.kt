package dam_a51696.recipebuddy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a favorite meal in the local database.
 * 
 * This class defines the schema for the "favorite_meals" table.
 * 
 * @property mealId Unique identifier for the meal, used as the primary key.
 * @property name The name of the meal.
 * @property imageUrl URL to the meal's thumbnail image.
 * @property category The category the meal belongs to (optional).
 * @property area The geographic area/origin of the meal (optional).
 * @property instructions Step-by-step preparation instructions.
 * @property ingredientsJson A JSON string representation of the ingredients list.
 * @property addedTimestamp The time when the meal was added to favorites, used for sorting.
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
