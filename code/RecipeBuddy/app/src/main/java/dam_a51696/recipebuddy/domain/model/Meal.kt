package dam_a51696.recipebuddy.domain.model

/**
 * Domain model representing a meal in a list view (e.g., search results or category lists).
 * 
 * This is a simplified version of the meal data, containing only essential info for display.
 * 
 * @property id Unique identifier for the meal.
 * @property name The name of the meal.
 * @property imageUrl URL to the meal's thumbnail image.
 */
data class Meal(
    val id: String,
    val name: String,
    val imageUrl: String
)
