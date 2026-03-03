package dam_a51696.recipebuddy.domain.model

/**
 * Domain model for detailed meal information
 */
data class MealDetail(
    val id: String,
    val name: String,
    val imageUrl: String,
    val instructions: String,
    val ingredients: List<Ingredient>
)

data class Ingredient(
    val name: String,
    val measure: String
)
