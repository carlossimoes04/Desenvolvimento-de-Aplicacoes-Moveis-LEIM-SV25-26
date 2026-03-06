package dam_a51696.recipebuddy.data.local

import com.google.gson.Gson
import dam_a51696.recipebuddy.data.local.dao.FavoriteMealDao
import dam_a51696.recipebuddy.data.local.entity.FavoriteMealEntity
import dam_a51696.recipebuddy.domain.model.MealDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Local data source for database operations
 */
class LocalDataSource @Inject constructor(
    private val favoriteMealDao: FavoriteMealDao,
    private val gson: Gson
) {
    fun getAllFavorites(): Flow<List<FavoriteMealEntity>> {
        return favoriteMealDao.getAllFavorites()
    }
    
    suspend fun getFavoriteById(mealId: String): FavoriteMealEntity? {
        return favoriteMealDao.getFavoriteById(mealId)
    }
    
    fun isFavorite(mealId: String): Flow<Boolean> {
        return favoriteMealDao.isFavorite(mealId)
    }
    
    suspend fun saveFavorite(meal: MealDetail) {
        val entity = FavoriteMealEntity(
            mealId = meal.id,
            name = meal.name,
            imageUrl = meal.imageUrl,
            category = null,
            area = null,
            instructions = meal.instructions,
            ingredientsJson = gson.toJson(meal.ingredients)
        )
        favoriteMealDao.insertFavorite(entity)
    }
    
    suspend fun removeFavorite(mealId: String) {
        favoriteMealDao.deleteFavoriteById(mealId)
    }
    
    suspend fun clearAllFavorites() {
        favoriteMealDao.deleteAllFavorites()
    }
}
