package dam_A51696.pantrychef.data.remote.api

import dam_A51696.pantrychef.data.remote.dto.MealResponseDto
import dam_A51696.pantrychef.data.remote.dto.RecipeDetailResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MealDbApi {
    
    // faz-se um pedido GET à terminação filter.php
    @GET("filter.php")
    suspend fun getRecipesByIngredient(
        // o "@Query("i")" vai transformar isto em -> filter.php?i=ingrediente_filtrado
        @Query("i") ingredient: String
    ): MealResponseDto

    // faz-se um pedido GET à terminação lookup.php
    @GET("lookup.php")
    suspend fun getRecipeById(
        // o "@Query("i")" vai transformar isto em -> lookup.php?i=id_da_receita
        @Query("i") id: String
    ): RecipeDetailResponseDto
}

/**
 * O Retrofit (configurada no Gradle) lê esta interface e 
 * escreve automaticamente todo o código para fazer um pedido seguro,
 * utilizando o suspend das coroutines,
 * garantindo que a interface da app não bloqueia enquanto se
 * espera pela resposta da API.
 */