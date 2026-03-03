package dam_a51696.recipebuddy.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for meal search response from TheMealDB API
 */
data class MealResponse(
    @SerializedName("meals")
    val meals: List<MealDto>?
)

data class MealDto(
    @SerializedName("idMeal")
    val idMeal: String,
    @SerializedName("strMeal")
    val strMeal: String,
    @SerializedName("strMealThumb")
    val strMealThumb: String
)
