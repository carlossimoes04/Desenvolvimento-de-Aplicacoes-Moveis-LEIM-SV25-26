package dam_A51696.pantrychef.data.remote.dto

import dam_A51696.pantrychef.domain.model.ShoppingItem

/**
 * Data Transfer Object para representar um item da lista de compras no Firebase
 *
 * @property id ID do item: pode ser nulo enquanto não for guardado no Firebase
 * @property name Nome do item
 * @property details Detalhes do item
 * @property bought Indica se o item já foi comprado
 */
data class ShoppingItemFirebaseDto(
    val id: String? = null,
    val name: String = "",
    val details: String = "",
    val bought: Boolean = false
)

/**
 * Converte dados do Firebase no modelo da aplicação
 *
 * @return Modelo ShoppingItem
 */
fun ShoppingItemFirebaseDto.toDomain(): ShoppingItem {
    return ShoppingItem(
        id = this.id ?: "",
        name = this.name,
        details = this.details,
        isBought = this.bought
    )
}

/**
 * Converte o modelo da aplicação em dados do Firebase
 *
 * @return Objeto ShoppingItemFirebaseDto
 */
fun ShoppingItem.toDto(): ShoppingItemFirebaseDto {
    return ShoppingItemFirebaseDto(
        id = this.id,
        name = this.name,
        details = this.details,
        bought = this.isBought
    )
}

/*
 * Fiz uma data class com valores por defeito em todos os campos porque o Firebase precisa de
 * um construtor sem argumentos para conseguir desserializar os dados
 *
 * O campo id é nullable porque quando se cria um item novo ele ainda não tem
 * identificador, e o Firebase gera esse identificador automaticamente ao guardar
 *
 * Criei também funções de extensão toDomain e toDto porque o modelo da aplicação (ShoppingItem)
 * não deve conhecer o Firebase, e estas funções fazem a conversão entre as duas camadas
 * sem misturar responsabilidades
 */
