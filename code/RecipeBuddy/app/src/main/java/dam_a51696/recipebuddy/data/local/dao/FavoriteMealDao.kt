package dam_a51696.recipebuddy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dam_a51696.recipebuddy.data.local.entity.FavoriteMealEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for favorite meals table
 */
@Dao
interface FavoriteMealDao {
    
    @Query("SELECT * FROM favorite_meals ORDER BY added_timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteMealEntity>>
    
    @Query("SELECT * FROM favorite_meals WHERE meal_id = :mealId")
    suspend fun getFavoriteById(mealId: String): FavoriteMealEntity?
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_meals WHERE meal_id = :mealId)")
    fun isFavorite(mealId: String): Flow<Boolean>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(meal: FavoriteMealEntity)
    
    @Delete
    suspend fun deleteFavorite(meal: FavoriteMealEntity)
    
    @Query("DELETE FROM favorite_meals WHERE meal_id = :mealId")
    suspend fun deleteFavoriteById(mealId: String)
    
    @Query("DELETE FROM favorite_meals")
    suspend fun deleteAllFavorites()
}
