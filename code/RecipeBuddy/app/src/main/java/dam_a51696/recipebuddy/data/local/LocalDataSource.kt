package dam_a51696.recipebuddy.data.local

import com.google.gson.Gson
import dam_a51696.recipebuddy.data.local.dao.FavoriteMealDao
import dam_a51696.recipebuddy.data.local.entity.FavoriteMealEntity
import dam_a51696.recipebuddy.domain.model.MealDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Local data source that handles database operations related to favorite meals.
 * 
 * This class acts as a mediator between the repository and the Room DAOs,
 * handling data conversion between domain models and database entities.
 * 
 * @property favoriteMealDao The DAO for accessing favorite meals in the database.
 * @property gson Gson instance for serializing and deserializing ingredients.
 */
class LocalDataSource @Inject constructor(
    private val favoriteMealDao: FavoriteMealDao,
    private val gson: Gson
) {
    /**
     * Retrieves all favorite meals from the database as a Flow.
     * 
     * @return A Flow emitting a list of [FavoriteMealEntity].
     */
    fun getAllFavorites(): Flow<List<FavoriteMealEntity>> {
        return favoriteMealDao.getAllFavorites()
    }
    
    /**
     * Retrieves a single favorite meal by its unique ID.
     * 
     * @param mealId The ID of the meal to retrieve.
     * @return The [FavoriteMealEntity] if found, null otherwise.
     */
    suspend fun getFavoriteById(mealId: String): FavoriteMealEntity? {
        return favoriteMealDao.getFavoriteById(mealId)
    }
    
    /**
     * Observes whether a specific meal is marked as favorite.
     * 
     * @param mealId The ID of the meal to check.
     * @return A Flow emitting true if the meal is a favorite, false otherwise.
     */
    fun isFavorite(mealId: String): Flow<Boolean> {
        return favoriteMealDao.isFavorite(mealId)
    }
    
    /**
     * Saves a meal to the favorites database.
     * 
     * @param meal The [MealDetail] domain model to save.
     */
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
    
    /**
     * Removes a meal from the favorites database by its ID.
     * 
     * @param mealId The ID of the meal to remove.
     */
    suspend fun removeFavorite(mealId: String) {
        favoriteMealDao.deleteFavoriteById(mealId)
    }
    
    /**
     * Deletes all favorite meals from the database.
     */
    suspend fun clearAllFavorites() {
        favoriteMealDao.deleteAllFavorites()
    }
}
