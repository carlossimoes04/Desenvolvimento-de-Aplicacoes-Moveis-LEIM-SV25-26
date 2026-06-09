package dam_A51696.pantrychef.data.remote.dto

import com.google.gson.annotations.SerializedName
import dam_A51696.pantrychef.domain.model.Recipe


/**
 * Data Transfer Object para receber a resposta da API
 *
 * @property meals Lista de refeições
 */
data class MealResponseDto(
    @SerializedName("meals")
    val meals: List<MealDto>? // Pode ser nulo se a API não encontrar receitas
)

/**
 * Data Transfer Object para representar os detalhes da refeição na API
 *
 * @property idMeal Identificador da refeição
 * @property strMeal Nome da refeição
 * @property strMealThumb Imagem da refeição
 */
data class MealDto(
    @SerializedName("idMeal")
    val idMeal: String,
    
    @SerializedName("strMeal")
    val strMeal: String,
    
    @SerializedName("strMealThumb")
    val strMealThumb: String
)

/**
 * Converte dados da API no modelo da aplicação
 *
 * @return Modelo Recipe
 */
fun MealDto.toDomain(): Recipe {
    return Recipe(
        idMeal = this.idMeal,
        strMeal = this.strMeal,
        strMealThumb = this.strMealThumb
    )
}

/**
 * Fiz data classes porque a API devolve informações em formato JSON e eu
 * precisei de mapear a estrutura, tal como em todos os ficheiros da pasta "dto".
 *
 * A data class MealResponseDto serve para receber a lista de elementos que a API devolve,
 * enquanto a data class MealDto serve para guardar os detalhes do elemento.
 *
 * Utilizei a anotação SerializedName porque a biblioteca Gson precisa de obter as
 * chaves do JSON mesmo que se decida mudar o nome das variáveis no código.
 *
 * Criei a função de extensão toDomain porque o resto da aplicação tem de trabalhar
 * apenas com o objeto Recipe e deve ignorar a existência da API.
 */