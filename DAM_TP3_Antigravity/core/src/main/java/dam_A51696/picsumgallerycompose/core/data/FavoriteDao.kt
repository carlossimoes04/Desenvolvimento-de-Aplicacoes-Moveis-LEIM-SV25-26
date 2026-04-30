package dam_A51696.picsumgallerycompose.core.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY timestamp ASC")
    fun getAllFavoritesFlow(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites ORDER BY timestamp ASC")
    suspend fun getAllFavorites(): List<FavoriteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity): Long

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun removeFavorite(id: String): Int

    @Query("SELECT COUNT(*) FROM favorites")
    suspend fun getCount(): Int

    @Query("DELETE FROM favorites WHERE id IN (SELECT id FROM favorites ORDER BY timestamp ASC LIMIT :limit)")
    suspend fun deleteOldest(limit: Int): Int
}
