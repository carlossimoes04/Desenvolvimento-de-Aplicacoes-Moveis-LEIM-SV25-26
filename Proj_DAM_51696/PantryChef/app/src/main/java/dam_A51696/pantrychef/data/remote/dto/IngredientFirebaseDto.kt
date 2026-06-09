package dam_A51696.pantrychef.data.remote.dto

import dam_A51696.pantrychef.domain.model.Ingredient


/**
 * Data Transfer Object para representar ingredientes no Firebase
 *
 * @property id Identificador do ingrediente
 * @property name Nome do ingrediente
 * @property quantity Quantidade do ingrediente
 * @property unit Unidade de medida
 * @property expirationDate Data de validade em milissegundos
 */
data class IngredientFirebaseDto(
    val id: String = "",
    val name: String = "",
    val quantity: Double = 0.0,
    val unit: String = "",
    val expirationDate: Long = 0L
)

/**
 * Converte dados do Firebase no modelo da aplicação
 *
 * @return Modelo Ingredient
 */
fun IngredientFirebaseDto.toDomain(): Ingredient {
    return Ingredient(
        id = this.id,
        name = this.name,
        quantity = this.quantity,
        unit = this.unit,
        expirationDate = this.expirationDate
    )
}

/**
 * Converte o modelo da aplicação em dados do Firebase
 *
 * @return Objeto IngredientFirebaseDto
 */
fun Ingredient.toDto(): IngredientFirebaseDto {
    return IngredientFirebaseDto(
        id = this.id,
        name = this.name,
        quantity = this.quantity,
        unit = this.unit,
        expirationDate = this.expirationDate
    )
}

/*
 * Fiz uma data class para transportar dados. O Kotlin gera funções.
 * A classe tem valores por defeito para garantir um construtor sem argumentos.
 * O Firebase precisa de um construtor sem argumentos.
 *
 * Criei funções de extensão para separar a lógica.
 * O modelo da aplicação desconhece o Firebase, no entanto, o DTO existe para o Firebase.
 * A função toDomain transforma dados do Firebase no modelo da aplicação.
 * A função toDto transforma o modelo da aplicação em dados do Firebase.
 */