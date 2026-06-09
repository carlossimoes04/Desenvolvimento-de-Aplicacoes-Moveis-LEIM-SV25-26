package dam_A51696.pantrychef.domain.model

data class RecipeDetail(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String,
    val strInstructions: String,
    val strYoutube: String,
    val ingredients: List<Pair<String, String>> // par ingrediente, medida
)
