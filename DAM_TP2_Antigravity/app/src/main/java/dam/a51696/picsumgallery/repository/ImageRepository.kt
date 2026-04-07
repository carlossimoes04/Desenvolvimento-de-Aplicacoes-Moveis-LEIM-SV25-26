package dam.a51696.picsumgallery.repository

import dam.a51696.picsumgallery.api.PicsumApiService
import dam.a51696.picsumgallery.data.CacheDao
import dam.a51696.picsumgallery.data.CacheEntity
import dam.a51696.picsumgallery.data.FavoriteDao
import dam.a51696.picsumgallery.data.FavoriteEntity
import dam.a51696.picsumgallery.model.ImageItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ImageRepository(
    private val apiService: PicsumApiService,
    private val favoriteDao: FavoriteDao,
    private val cacheDao: CacheDao
) {

    suspend fun getImages(page: Int = 1, limit: Int = 30): Result<List<ImageItem>> {
        return try {
            val images = apiService.getImages(page, limit)
            
            // Phase 4: Local Cache Sync
            if (page == 1) {
               cacheDao.clearCache()
            }
            val cacheList = images.mapIndexed { index, img -> 
                CacheEntity(img.id, img.author, img.downloadUrl, page * 100 + index) 
            }
            cacheDao.insertCache(cacheList) 
            Result.success(images)
        } catch (e: Exception) {
            // Offline Fallback
            val offlineItems = cacheDao.getAllCachedFlow().firstOrNull()
            if (!offlineItems.isNullOrEmpty()) {
                val mapped = offlineItems.map { ImageItem(it.id, it.author, it.downloadUrl) }
                Result.success(mapped)
            } else {
                Result.failure(e)
            }
        }
    }

    // Phase 3: Favorites FIFO Logic
    fun getAllFavoritesFlow(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavoritesFlow()
    }

    suspend fun toggleFavorite(imageItem: ImageItem) {
        val currentFavorites = favoriteDao.getAllFavorites()
        val exists = currentFavorites.any { it.id == imageItem.id }
        
        if (exists) {
            favoriteDao.removeFavorite(imageItem.id)
        } else {
            val count = favoriteDao.getCount()
            if (count >= 5) {
                // Remove o registo mais antigo pela timestamp (FIFO Strict 5 elements)
                favoriteDao.deleteOldest(1)
            }
            favoriteDao.insertFavorite(
                FavoriteEntity(
                    id = imageItem.id,
                    author = imageItem.author,
                    downloadUrl = imageItem.downloadUrl
                )
            )
        }
    }
}
