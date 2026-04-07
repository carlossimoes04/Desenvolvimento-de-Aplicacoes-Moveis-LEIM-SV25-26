package dam.a51696.picsumgallery.repository

import dam.a51696.picsumgallery.api.PicsumApiService
import dam.a51696.picsumgallery.model.ImageItem

class ImageRepository(private val apiService: PicsumApiService) {

    suspend fun getImages(page: Int = 1, limit: Int = 30): Result<List<ImageItem>> {
        return try {
            // Utilizamos Kotlin Result para encapsular sucessos e falhas facilmente.
            val images = apiService.getImages(page, limit)
            Result.success(images)
        } catch (e: Exception) {
            // Se houver problemas de rede (ex: sem timeout, parse error, sem wifi),
            // apanhamos a Exception e retornamos uma falha.
            Result.failure(e)
        }
    }
}
