package dam_A51696.pantrychef.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dam_A51696.pantrychef.core.utils.Constants
import dam_A51696.pantrychef.data.remote.api.MealDbApi
import dam_A51696.pantrychef.data.repository.PantryRepositoryImpl
import dam_A51696.pantrychef.data.repository.RecipeRepositoryImpl
import dam_A51696.pantrychef.domain.repository.PantryRepository
import dam_A51696.pantrychef.domain.repository.RecipeRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMealDbApi(retrofit: Retrofit): MealDbApi {
        return retrofit.create(MealDbApi::class.java)
    }

    @Provides
    @Singleton
    fun providePantryRepository(): PantryRepository {
        return PantryRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(api: MealDbApi): RecipeRepository {
        return RecipeRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideShoppingRepository(): dam_A51696.pantrychef.domain.repository.ShoppingRepository {
        return dam_A51696.pantrychef.data.repository.ShoppingRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(): dam_A51696.pantrychef.domain.repository.FavoriteRepository {
        return dam_A51696.pantrychef.data.repository.FavoriteRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): dam_A51696.pantrychef.domain.repository.AuthRepository {
        return dam_A51696.pantrychef.data.repository.AuthRepositoryImpl()
    }
}
