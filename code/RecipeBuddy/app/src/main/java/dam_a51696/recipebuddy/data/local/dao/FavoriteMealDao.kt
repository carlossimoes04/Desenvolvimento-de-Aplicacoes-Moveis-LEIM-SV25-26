package dam_a51696.recipebuddy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dam_a51696.recipebuddy.data.local.entity.FavoriteMealEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing the favorite meals table in the local database.
 * 
 * This interface defines the SQL queries and operations for favorite recipes.
 */
@Dao
interface FavoriteMealDao {
    
    /**
     * Retrieves all favorite meals ordered by the time they were added (most recent first).
     * 
     * @return A Flow emitting a list of [FavoriteMealEntity].
     */
    @Query("SELECT * FROM favorite_meals ORDER BY added_timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteMealEntity>>
    
    /**
     * Retrieves a specific favorite meal by its ID.
     * 
     * @param mealId The unique ID of the meal.
     * @return The [FavoriteMealEntity] if it exists, null otherwise.
     */
    @Query("SELECT * FROM favorite_meals WHERE meal_id = :mealId")
    suspend fun getFavoriteById(mealId: String): FavoriteMealEntity?
    
    /**
     * Checks if a meal exists in the favorites table.
     * 
     * @param mealId The unique ID of the meal.
     * @return A Flow emitting true if the meal is a favorite, false otherwise.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_meals WHERE meal_id = :mealId)")
    fun isFavorite(mealId: String): Flow<Boolean>
    
    /**
     * Inserts a new favorite meal or replaces an existing one if the ID conflicts.
     * 
     * @param meal The [FavoriteMealEntity] to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(meal: FavoriteMealEntity)
    
    /**
     * Deletes a specific favorite meal entity from the database.
     * 
     * @param meal The entity to delete.
     */
    @Delete
    suspend fun deleteFavorite(meal: FavoriteMealEntity)
    
    /**
     * Deletes a favorite meal from the database by its ID.
     * 
     * @param mealId The unique ID of the meal to remove.
     */
    @Query("DELETE FROM favorite_meals WHERE meal_id = :mealId")
    suspend fun deleteFavoriteById(mealId: String)
    
    /**
     * Deletes all favorite meals from the database.
     */
    @Query("DELETE FROM favorite_meals")
    suspend fun deleteAllFavorites()
}
