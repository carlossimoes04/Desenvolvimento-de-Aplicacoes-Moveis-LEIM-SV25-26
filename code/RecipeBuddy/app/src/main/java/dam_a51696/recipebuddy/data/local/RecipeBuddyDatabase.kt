package dam_a51696.recipebuddy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dam_a51696.recipebuddy.data.local.dao.FavoriteMealDao
import dam_a51696.recipebuddy.data.local.entity.FavoriteMealEntity

/**
 * Main Room Database class for the Recipe Buddy application.
 * 
 * This database manages the local storage of favorite meals.
 */
@Database(
    entities = [FavoriteMealEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RecipeBuddyDatabase : RoomDatabase() {
    /**
     * Provides access to the [FavoriteMealDao] for interacting with favorite meals.
     * 
     * @return The [FavoriteMealDao] implementation.
     */
    abstract fun favoriteMealDao(): FavoriteMealDao
}
