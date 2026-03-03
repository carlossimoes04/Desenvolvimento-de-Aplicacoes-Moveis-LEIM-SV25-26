package dam_a51696.recipebuddy.di

import dam_a51696.recipebuddy.data.api.MealDbApiService
import dam_a51696.recipebuddy.data.repository.MealRepository
import dam_a51696.recipebuddy.data.repository.MealRepositoryImpl
import dam_a51696.recipebuddy.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dependency injection module for network operations
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.THEMEALDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideMealDbApiService(retrofit: Retrofit): MealDbApiService {
        return retrofit.create(MealDbApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideMealRepository(impl: MealRepositoryImpl): MealRepository {
        return impl
    }
}
