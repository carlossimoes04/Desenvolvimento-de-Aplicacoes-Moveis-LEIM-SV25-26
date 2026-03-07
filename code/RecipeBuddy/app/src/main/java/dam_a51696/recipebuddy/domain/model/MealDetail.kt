package dam_a51696.recipebuddy.domain.model

/**
 * Domain model representing the full details of a meal.
 * 
 * This model is used for displaying meal information on the detail screen
 * and for saving meals to favorites.
 * 
 * @property id Unique identifier for the meal.
 * @property name The name of the meal.
 * @property imageUrl URL to the meal's image.
 * @property instructions Step-by-step cooking instructions.
 * @property ingredients List of [Ingredient] objects required for the meal.
 */
data class MealDetail(
    val id: String,
    val name: String,
    val imageUrl: String,
    val instructions: String,
    val ingredients: List<Ingredient>
)

/**
 * Domain model representing a single ingredient and its corresponding measure.
 * 
 * @property name The name of the ingredient (e.g., "Flour").
 * @property measure The quantity or description (e.g., "2 cups").
 */
data class Ingredient(
    val name: String,
    val measure: String
)
