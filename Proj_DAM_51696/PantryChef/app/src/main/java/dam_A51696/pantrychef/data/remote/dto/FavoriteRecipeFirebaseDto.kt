package dam_A51696.pantrychef.data.remote.dto

import dam_A51696.pantrychef.domain.model.Recipe

// o DTO com os campos necessários, todos com valores padrão = "" para que o Firebase consiga ler os dados
data class FavoriteRecipeFirebaseDto(
    val idMeal: String = "",
    val strMeal: String = "",
    val strMealThumb: String = ""
)

// converte do modelo (Recipe) para o formato do Firebase
fun Recipe.toFavoriteDto(): FavoriteRecipeFirebaseDto {
    return FavoriteRecipeFirebaseDto(idMeal = idMeal, strMeal = strMeal, strMealThumb = strMealThumb)
}

// converte do Firebase de volta para o Modelo (Recipe)
fun FavoriteRecipeFirebaseDto.toDomain(): Recipe {
    return Recipe(idMeal = idMeal, strMeal = strMeal, strMealThumb = strMealThumb)
}