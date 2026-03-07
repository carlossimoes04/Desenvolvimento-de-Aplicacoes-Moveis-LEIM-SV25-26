package dam_a51696.recipebuddy.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import dam_a51696.recipebuddy.data.local.LocalDataSource
import dam_a51696.recipebuddy.data.local.RecipeBuddyDatabase
import dam_a51696.recipebuddy.data.local.dao.FavoriteMealDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies.
 * 
 * This module provides the Room database, DAOs, and the local data source.
 * All provided dependencies are scoped to the [SingletonComponent].
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides the singleton instance of [RecipeBuddyDatabase].
     * 
     * @param context The application context.
     * @return The built [RecipeBuddyDatabase].
     */
    @Provides
    @Singleton
    fun provideRecipeBuddyDatabase(
        @ApplicationContext context: Context
    ): RecipeBuddyDatabase {
        return Room.databaseBuilder(
            context,
            RecipeBuddyDatabase::class.java,
            "recipe_buddy_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    /**
     * Provides the [FavoriteMealDao] from the database.
     * 
     * @param database The database instance.
     * @return The [FavoriteMealDao] for accessing favorite meals.
     */
    @Provides
    @Singleton
    fun provideFavoriteMealDao(database: RecipeBuddyDatabase): FavoriteMealDao {
        return database.favoriteMealDao()
    }
    
    /**
     * Provides a singleton [Gson] instance.
     * 
     * @return A [Gson] instance for JSON processing.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
    
    /**
     * Provides the [LocalDataSource] for local data operations.
     * 
     * @param favoriteMealDao The DAO for favorite meals.
     * @param gson The Gson instance for serialization.
     * @return A singleton instance of [LocalDataSource].
     */
    @Provides
    @Singleton
    fun provideLocalDataSource(
        favoriteMealDao: FavoriteMealDao,
        gson: Gson
    ): LocalDataSource {
        return LocalDataSource(favoriteMealDao, gson)
    }
}
