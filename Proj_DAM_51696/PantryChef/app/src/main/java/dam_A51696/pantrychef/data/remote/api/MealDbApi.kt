package dam_A51696.pantrychef.data.remote.api

import dam_A51696.pantrychef.data.remote.dto.MealResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MealDbApi {
    
    // Fazemos um pedido GET à terminação filter.php
    @GET("filter.php")
    suspend fun getRecipesByIngredient(
        // O "@Query("i")" vai transformar isto em -> filter.php?i=o_teu_ingrediente
        @Query("i") ingredient: String
    ): MealResponseDto

    @GET("lookup.php")
    suspend fun getRecipeById(
        @Query("i") id: String
    ): dam_A51696.pantrychef.data.remote.dto.RecipeDetailResponseDto
}

/**
 * O Retrofit (configurada no Gradle) lê esta interface e 
 * escreve automaticamente todo o código por baixo dos panos para fazer um pedido seguro, 
 * no fio secundário (graças ao suspend das coroutines), garantindo que a interface da app 
 * não bloqueia enquanto esperamos pela resposta da API.
 */