package dam.a51696.picsumgallery.model

import com.google.gson.annotations.SerializedName

data class ImageItem(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("author")
    val author: String,
    
    @SerializedName("download_url")
    val downloadUrl: String
)
