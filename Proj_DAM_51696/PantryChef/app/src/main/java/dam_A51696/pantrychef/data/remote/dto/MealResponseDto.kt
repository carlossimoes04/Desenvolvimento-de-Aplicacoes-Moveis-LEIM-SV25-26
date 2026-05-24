package dam_A51696.pantrychef.data.remote.dto

import com.google.gson.annotations.SerializedName
import dam_A51696.pantrychef.domain.model.Recipe

// O objeto principal que contém a lista
data class MealResponseDto(
    @SerializedName("meals")
    val meals: List<MealDto>? // Pode ser nulo se a API não encontrar receitas
)

// O objeto individual de cada receita
data class MealDto(
    @SerializedName("idMeal")
    val idMeal: String,
    
    @SerializedName("strMeal")
    val strMeal: String,
    
    @SerializedName("strMealThumb")
    val strMealThumb: String
)

// Função Mapper para converter o objeto da API no modelo
fun MealDto.toDomain(): Recipe {
    return Recipe(
        idMeal = this.idMeal,
        strMeal = this.strMeal,
        strMealThumb = this.strMealThumb
    )
}

/**
 * A anotação @SerializedName("...") garante que mesmo que se decida mudar o nome das variáveis no futuro 
 * (ex: mudar strMealThumb para imagem), a biblioteca Gson (usada pelo Retrofit) vai saber encontrar 
 * o valor original no JSON da API. O toDomain() é crucial para que o resto da aplicação trabalhe apenas 
 * com o objeto "Recipe", ignorando a existência da internet/API.
 */