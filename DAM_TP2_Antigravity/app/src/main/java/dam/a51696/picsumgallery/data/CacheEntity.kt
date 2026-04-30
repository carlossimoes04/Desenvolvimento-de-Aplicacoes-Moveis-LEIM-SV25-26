package dam.a51696.picsumgallery.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache")
data class CacheEntity(
    @PrimaryKey val id: String,
    val author: String,
    val downloadUrl: String,
    val orderIndex: Int
)
