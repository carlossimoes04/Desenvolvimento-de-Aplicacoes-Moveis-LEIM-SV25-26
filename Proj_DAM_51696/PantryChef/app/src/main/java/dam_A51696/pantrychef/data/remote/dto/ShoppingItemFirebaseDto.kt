package dam_A51696.pantrychef.data.remote.dto

import dam_A51696.pantrychef.domain.model.ShoppingItem

data class ShoppingItemFirebaseDto(
    val id: String? = null,
    val name: String = "",
    val details: String = "",
    val isBought: Boolean = false
)

fun ShoppingItemFirebaseDto.toDomain(): ShoppingItem {
    return ShoppingItem(
        id = this.id ?: "",
        name = this.name,
        details = this.details,
        isBought = this.isBought
    )
}

fun ShoppingItem.toDto(): ShoppingItemFirebaseDto {
    return ShoppingItemFirebaseDto(
        id = this.id,
        name = this.name,
        details = this.details,
        isBought = this.isBought
    )
}
