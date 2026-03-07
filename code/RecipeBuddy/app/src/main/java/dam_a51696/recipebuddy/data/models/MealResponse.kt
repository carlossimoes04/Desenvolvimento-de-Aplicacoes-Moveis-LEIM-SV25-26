package dam_a51696.recipebuddy.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for the response from TheMealDB API search and filter endpoints.
 * 
 * @property meals A list of [MealDto] matching the query, or null if no results are found.
 */
data class MealResponse(
    @SerializedName("meals")
    val meals: List<MealDto>?
)

/**
 * Data Transfer Object representing a single meal in a list response.
 * 
 * @property idMeal The unique identifier of the meal.
 * @property strMeal The name of the meal.
 * @property strMealThumb URL to the meal's thumbnail image.
 */
data class MealDto(
    @SerializedName("idMeal")
    val idMeal: String,
    @SerializedName("strMeal")
    val strMeal: String,
    @SerializedName("strMealThumb")
    val strMealThumb: String
)
