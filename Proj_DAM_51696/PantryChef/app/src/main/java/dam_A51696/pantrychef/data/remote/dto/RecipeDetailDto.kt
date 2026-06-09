package dam_A51696.pantrychef.data.remote.dto

import dam_A51696.pantrychef.domain.model.RecipeDetail

/**
 * Data Transfer Object para receber a resposta de detalhe de uma receita da API
 *
 * A API devolve os campos como um mapa de chave-valor em vez de campos fixos,
 * porque os ingredientes e medidas são numerados dinamicamente
 * (strIngredient1, strIngredient2, etc.)
 *
 * @property meals Lista de mapas com os dados da receita, pode ser nula se a API não encontrar resultados
 */
data class RecipeDetailResponseDto(
    val meals: List<Map<String, String?>>?
)

/**
 * Converte a resposta da API para o modelo de domínio [RecipeDetail]
 *
 * Percorre os campos numerados de 1 a 20 para extrair pares de
 * ingrediente e medida, ignorando entradas vazias ou nulas
 *
 * @return O modelo [RecipeDetail] ou null se não existirem dados
 */
fun RecipeDetailResponseDto.toDomain(): RecipeDetail? {
    // extrai o primeiro mapa da lista em segurança;
    // se a lista for nula ou vazia, aborta a conversão devolvendo null
    val mealMap = meals?.firstOrNull() ?: return null

    // ----- campos obrigatórios -----

    // obtém o id da receita;
    // se for nulo, os dados são inválidos, pelo que se aborta a conversão
    val idMeal = mealMap["idMeal"] ?: return null

    // obtém o nome da receita;
    // campo obrigatório, aborta a conversão se estiver em falta
    val strMeal = mealMap["strMeal"] ?: return null

    // -----

    // ----- campos opcionais -----

    // obtém o URL da imagem;
    // se não houver link da imagem, usa uma string vazia
    // de maneira evitar nulos na ui
    val strMealThumb = mealMap["strMealThumb"] ?: ""

    // obtém as instruções de preparação;
    // se faltarem, as instruções como vazio em vez de null
    val strInstructions = mealMap["strInstructions"] ?: ""

    // obtém o URL do vídeo do youtube;
    // se não existir, assume o link como vazio como valor padrão
    val strYoutube = mealMap["strYoutube"] ?: ""

    // -----

    val ingredients = mutableListOf<Pair<String, String>>()

    for (i in 1..20) { // no máximo a API devolve 20 ingredientes
        val ingredient = mealMap["strIngredient$i"]
        val measure = mealMap["strMeasure$i"]
        
        if (!ingredient.isNullOrBlank()) {
            ingredients.add(Pair(ingredient.trim(), measure?.trim() ?: ""))
        }
    }

    return RecipeDetail(
        idMeal = idMeal,
        strMeal = strMeal,
        strMealThumb = strMealThumb,
        strInstructions = strInstructions,
        strYoutube = strYoutube,
        ingredients = ingredients
    )
}

/*
 * Fiz uma data class com Map<String, String?> em vez de campos fixos porque a API devolve
 * os ingredientes e medidas como campos numerados (strIngredient1 até strIngredient20),
 * e criar 40 variáveis separadas tornaria o código extenso.
 *
 * Criei a função de extensão toDomain porque o resto da aplicação tem de trabalhar apenas com o
 * objeto RecipeDetail e deve ignorar a existência da API, e também porque é nesta função que faço
 * a lógica de percorrer os 20 campos numerados e juntá-los em pares de ingrediente e medida.
 *
 * Coloquei no máximo 20 campos, porque, por exemplo, na receita:
 * https://www.themealdb.com/api/json/v1/1/lookup.php?i=52772, percebe-se que
 * há no máximo 20 ingredientes e 20 medidas por receita:
 * "(...) "strIngredient1":"soy sauce","strIngredient2":"water", (...) "strIngredient19":null,
 * "strIngredient20":null,"strMeasure1":"3\/4 cup","strMeasure2":"1\/2 cup", (...)
 * "strMeasure19":null,"strMeasure20":null (...)"
 */
