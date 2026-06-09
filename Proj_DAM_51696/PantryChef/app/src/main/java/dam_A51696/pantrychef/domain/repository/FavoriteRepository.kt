package dam_A51696.pantrychef.domain.repository

import dam_A51696.pantrychef.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define as operações para gerir as receitas favoritas do utilizador
 *
 * Serve como contrato para a camada de apresentação interagir com os favoritos
 */
interface FavoriteRepository {
    
    /**
     * Obtém as receitas favoritas do utilizador em tempo real
     *
     * @return Um [Flow] contendo a lista de receitas do tipo [Recipe]
     */
    fun getFavorites(): Flow<List<Recipe>>
    
    /**
     * Adiciona uma receita aos favoritos do utilizador
     *
     * @param recipe A receita a adicionar
     */
    suspend fun addFavorite(recipe: Recipe)
    
    /**
     * Remove uma receita dos favoritos do utilizador
     *
     * @param recipeId O identificador da receita a remover
     */
    suspend fun removeFavorite(recipeId: String)
    
    /**
     * Verifica em tempo real se uma receita está marcada como favorita
     *
     * @param recipeId O identificador da receita a verificar
     * @return Um [Flow] que emite true se a receita estiver nos favoritos, false caso contrário
     */
    fun isFavorite(recipeId: String): Flow<Boolean>
}

/*
 * Criei esta interface na camada de domínio para gerir as receitas marcadas como
 * favoritos sem acoplar a aplicação ao Firebase Realtime Database
 *
 * Utilizo Flow nas funções getFavorites e isFavorite porque o estado dos favoritos
 * sofre alterações na base de dados e a aplicação precisa de receber atualizações
 * sempre que há mudanças para redesenhar os ecrãs sem que o utilizador tenha de fazer ações
 *
 * As operações addFavorite e removeFavorite são funções suspend porque são ações de escrita
 * que ocorrem apenas uma vez e demoram tempo a concluir na rede, pelo que não podem causar
 * bloqueios na aplicação
 */