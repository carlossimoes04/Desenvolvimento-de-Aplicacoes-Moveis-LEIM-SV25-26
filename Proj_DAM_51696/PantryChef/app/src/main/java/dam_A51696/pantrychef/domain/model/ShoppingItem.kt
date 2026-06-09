package dam_A51696.pantrychef.domain.model

data class ShoppingItem(
    val id: String, // identificador único do item
    val name: String, // nome do item
    val details: String, // detalhes do item
    val isBought: Boolean // indica se o item foi comprado
)
