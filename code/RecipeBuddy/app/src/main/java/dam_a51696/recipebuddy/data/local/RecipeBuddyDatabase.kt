package dam_a51696.recipebuddy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dam_a51696.recipebuddy.data.local.dao.FavoriteMealDao
import dam_a51696.recipebuddy.data.local.entity.FavoriteMealEntity

/**
 * Room Database for Recipe Buddy application
 */
@Database(
    entities = [FavoriteMealEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RecipeBuddyDatabase : RoomDatabase() {
    abstract fun favoriteMealDao(): FavoriteMealDao
}
