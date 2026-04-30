package dam.a51696.picsumgallery.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val author: String,
    val downloadUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)
