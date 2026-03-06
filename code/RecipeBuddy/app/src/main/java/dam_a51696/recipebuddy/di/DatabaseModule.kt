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
 * Dependency injection module for Room Database
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
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
    
    @Provides
    @Singleton
    fun provideFavoriteMealDao(database: RecipeBuddyDatabase): FavoriteMealDao {
        return database.favoriteMealDao()
    }
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
    
    @Provides
    @Singleton
    fun provideLocalDataSource(
        favoriteMealDao: FavoriteMealDao,
        gson: Gson
    ): LocalDataSource {
        return LocalDataSource(favoriteMealDao, gson)
    }
}
