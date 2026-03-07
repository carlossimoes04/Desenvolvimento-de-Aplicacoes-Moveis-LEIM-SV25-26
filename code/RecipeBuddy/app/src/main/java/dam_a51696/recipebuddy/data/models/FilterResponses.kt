package dam_a51696.recipebuddy.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for the list of categories returned by TheMealDB API.
 * 
 * @property categories A list of [CategoryDto] objects.
 */
data class CategoryListResponse(
    @SerializedName("meals")
    val categories: List<CategoryDto>?
)

/**
 * Data Transfer Object representing a single category.
 * 
 * @property name The name of the category (e.g., "Beef", "Dessert").
 */
data class CategoryDto(
    @SerializedName("strCategory")
    val name: String
)

/**
 * Data Transfer Object for the list of areas (origins) returned by TheMealDB API.
 * 
 * @property areas A list of [AreaDto] objects.
 */
data class AreaListResponse(
    @SerializedName("meals")
    val areas: List<AreaDto>?
)

/**
 * Data Transfer Object representing a single area.
 * 
 * @property name The name of the area (e.g., "Italian", "Chinese").
 */
data class AreaDto(
    @SerializedName("strArea")
    val name: String
)

/**
 * Data Transfer Object for the list of ingredients returned by TheMealDB API.
 * 
 * @property ingredients A list of [IngredientDto] objects.
 */
data class IngredientListResponse(
    @SerializedName("meals")
    val ingredients: List<IngredientDto>?
)

/**
 * Data Transfer Object representing a single ingredient.
 * 
 * @property name The name of the ingredient (e.g., "Chicken", "Salt").
 */
data class IngredientDto(
    @SerializedName("strIngredient")
    val name: String
)
