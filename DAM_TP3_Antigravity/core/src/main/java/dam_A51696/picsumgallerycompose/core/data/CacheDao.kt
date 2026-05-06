package dam_A51696.picsumgallerycompose.core.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CacheDao {
    @Query("SELECT * FROM cache ORDER BY orderIndex ASC")
    fun getAllCachedFlow(): Flow<List<CacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(items: List<CacheEntity>): List<Long>

    @Query("DELETE FROM cache")
    suspend fun clearCache(): Int
}
