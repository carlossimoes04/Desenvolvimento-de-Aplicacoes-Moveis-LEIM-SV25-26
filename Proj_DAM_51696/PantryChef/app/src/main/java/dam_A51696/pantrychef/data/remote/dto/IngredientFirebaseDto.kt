package dam_A51696.pantrychef.data.remote.dto

import dam_A51696.pantrychef.domain.model.Ingredient

// O Firebase precisa que todos os campos tenham um valor por defeito para ter um construtor vazio
data class IngredientFirebaseDto(
    val id: String = "",
    val name: String = "",
    val quantity: Double = 0.0,
    val unit: String = "",
    val expirationDate: Long = 0L
)

// Conversão do formato Firebase para o modelo da App
fun IngredientFirebaseDto.toDomain(): Ingredient {
    return Ingredient(
        id = this.id,
        name = this.name,
        quantity = this.quantity,
        unit = this.unit,
        expirationDate = this.expirationDate
    )
}

// Conversão do modelo da App para o formato Firebase
fun Ingredient.toDto(): IngredientFirebaseDto {
    return IngredientFirebaseDto(
        id = this.id,
        name = this.name,
        quantity = this.quantity,
        unit = this.unit,
        expirationDate = this.expirationDate
    )
}