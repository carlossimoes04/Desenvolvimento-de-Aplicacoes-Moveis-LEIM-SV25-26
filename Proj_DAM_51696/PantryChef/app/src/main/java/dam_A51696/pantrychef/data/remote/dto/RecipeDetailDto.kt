package dam_A51696.pantrychef.data.remote.dto

import dam_A51696.pantrychef.domain.model.RecipeDetail

data class RecipeDetailResponseDto(
    val meals: List<Map<String, String?>>?
)

fun RecipeDetailResponseDto.toDomain(): RecipeDetail? {
    val mealMap = meals?.firstOrNull() ?: return null
    
    val idMeal = mealMap["idMeal"] ?: return null
    val strMeal = mealMap["strMeal"] ?: return null
    val strMealThumb = mealMap["strMealThumb"] ?: ""
    val strInstructions = mealMap["strInstructions"] ?: ""
    val strYoutube = mealMap["strYoutube"] ?: ""

    val ingredients = mutableListOf<Pair<String, String>>()
    
    // The API returns up to 20 ingredients and measures as strIngredient1, strMeasure1, etc.
    for (i in 1..20) {
        val ingredient = mealMap["strIngredient$i"]
        val measure = mealMap["strMeasure$i"]
        
        if (!ingredient.isNullOrBlank()) {
            ingredients.add(Pair(ingredient.trim(), measure?.trim() ?: ""))
        }
    }

    return RecipeDetail(
        idMeal = idMeal,
        strMeal = strMeal,
        strMealThumb = strMealThumb,
        strInstructions = strInstructions,
        strYoutube = strYoutube,
        ingredients = ingredients
    )
}
