package dam_A51696.pantrychef.domain.model

data class Ingredient(
    val id: String, // identificador único do ingrediente
    val name: String, // nome do ingrediente
    val quantity: Double, // ex: 1.5, 2.0
    val unit: String, // kg, g, ml, l, unidades, etc
    val expirationDate: Long // timestamp
)
