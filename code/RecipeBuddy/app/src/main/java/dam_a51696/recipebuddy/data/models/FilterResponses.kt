package dam_a51696.recipebuddy.data.models

import com.google.gson.annotations.SerializedName

/**
 * Response for category list endpoint
 */
data class CategoryListResponse(
    @SerializedName("meals")
    val categories: List<CategoryDto>?
)

data class CategoryDto(
    @SerializedName("strCategory")
    val name: String
)

/**
 * Response for area list endpoint
 */
data class AreaListResponse(
    @SerializedName("meals")
    val areas: List<AreaDto>?
)

data class AreaDto(
    @SerializedName("strArea")
    val name: String
)

/**
 * Response for ingredient list endpoint
 */
data class IngredientListResponse(
    @SerializedName("meals")
    val ingredients: List<IngredientDto>?
)

data class IngredientDto(
    @SerializedName("strIngredient")
    val name: String
)
