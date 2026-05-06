package dam_A51696.picsumgallerycompose.core.network

import dam_A51696.picsumgallerycompose.core.model.ImageItem
import retrofit2.http.GET
import retrofit2.http.Query

interface PicsumApiService {
    @GET("v2/list")
    suspend fun getImages(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 30
    ): List<ImageItem>
}
