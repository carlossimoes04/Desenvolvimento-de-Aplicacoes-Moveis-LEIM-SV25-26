package dam_A51696.pantrychef.data.remote.dto

import dam_A51696.pantrychef.domain.model.Recipe

/**
 * Data Transfer Object (DTO) para representar uma receita favorita no Firebase
 *
 * Esta classe é utilizada para ler e escrever dados de receitas favoritas
 * na base de dados do Firebase, neste caso, para o Realtime Database
 *
 * Possui valores padrão para todos os campos para permitir que o Firebase
 * utilize um construtor vazio durante a desserialização dos dados
 *
 * @property idMeal O ID único da receita
 * @property strMeal O nome da receita
 * @property strMealThumb O URL para a imagem (thumbnail) da receita
 */
data class FavoriteRecipeFirebaseDto(
    val idMeal: String = "", // id da receita
    val strMeal: String = "", // nome da receita
    val strMealThumb: String = "" // imagem da receita
)

/**
 * Função de extensão para converter o modelo de domínio [Recipe]
 * num DTO do Firebase [FavoriteRecipeFirebaseDto]
 *
 * Utilizada quando se quer guardar uma receita (que se usa na lógica da app)
 * na base de dados do Firebase
 *
 * @return O objeto [FavoriteRecipeFirebaseDto] formatado para o Firebase
 */
fun Recipe.toFavoriteDto(): FavoriteRecipeFirebaseDto {
    return FavoriteRecipeFirebaseDto(idMeal = idMeal, strMeal = strMeal, strMealThumb = strMealThumb)
}

/**
 * Função de extensão para converter um DTO do Firebase [FavoriteRecipeFirebaseDto]
 * de volta para o modelo de domínio [Recipe].
 *
 * Utilizada ao ler dados do Firebase para os transformar no modelo que
 * a aplicação entende e utiliza na sua lógica de negócio/UI.
 *
 * @return O objeto de domínio [Recipe] pronto a ser usado pela app
 */
fun FavoriteRecipeFirebaseDto.toDomain(): Recipe {
    return Recipe(idMeal = idMeal, strMeal = strMeal, strMealThumb = strMealThumb)
}

/*
 * Porque se criou uma data class?
 * - As 'data classes' são a estrutura ideal em Kotlin para criar Data Transfer
 *   Objects (DTOs), pois o seu único propósito é reter e transportar dados.
 *   O Kotlin poupa tempo ao programador, pois gera automaticamente métodos úteis
 *   como o equals(), o hashCode(), o toString() e o copy().
 * - Além disso, ao fornecer valores padrão (="") para todos os parâmetros,
 *   o Kotlin cria por trás um construtor sem argumentos (no-args constructor).
 *   Isto é uma exigência estrita do SDK do Firebase para conseguir converter
 *   (desserializar) os documentos vindos da base de dados em objetos Kotlin.
 *
 * Porque se criaram funções de extensão?
 * - O modelo central ('Recipe') não deve ter conhecimento de que o Firebase sequer
 *   existe. Por outro lado, o DTO existe apenas para servir a base de dados.
 * - As funções de extensão toDomain e toFavoriteDto criam uma ponte entre a
 *   camada de dados (Firebase) e a camada de domínio (App).
 * - Usar funções de extensão é a forma mais geral em Kotlin de o fazer,
 *   permitindo chamadas no código, como: `recipe.toFavoriteDto()`.
 */