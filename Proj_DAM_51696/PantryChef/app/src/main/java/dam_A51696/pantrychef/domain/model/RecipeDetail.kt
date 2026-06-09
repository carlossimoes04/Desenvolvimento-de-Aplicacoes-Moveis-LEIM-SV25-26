package dam_A51696.pantrychef.domain.model

data class RecipeDetail(
    val idMeal: String, // identificador único da receita
    val strMeal: String, // nome da receita
    val strMealThumb: String, // URL da imagem da receita
    val strInstructions: String, // instruções da receita
    val strYoutube: String, // URL do vídeo da receita
    val ingredients: List<Pair<String, String>> // par ingrediente, medida
)
