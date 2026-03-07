package dam_a51696.recipebuddy.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dam_a51696.recipebuddy.data.datasource.RemoteDataSource
import dam_a51696.recipebuddy.data.local.LocalDataSource
import dam_a51696.recipebuddy.data.models.MealDetailResponse
import dam_a51696.recipebuddy.data.models.MealResponse
import dam_a51696.recipebuddy.domain.model.Ingredient
import dam_a51696.recipebuddy.domain.model.MealDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import java.lang.reflect.Type

/**
 * Implementation of [MealRepository] that coordinates data from remote and local sources.
 * 
 * This class uses [RemoteDataSource] for network requests and [LocalDataSource] for 
 * database operations. It handles exception wrapping and data mapping.
 * 
 * @property remoteDataSource Source for fetching data from the API.
 * @property localDataSource Source for persistent storage of favorite meals.
 * @property gson Gson instance for serializing and deserializing domain models.
 */
class MealRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val gson: Gson
) : MealRepository {
    
    override suspend fun searchMeals(query: String): Result<MealResponse> {
        return try {
            if (query.isBlank()) {
                Result.failure(IllegalArgumentException("Search query cannot be empty"))
            } else {
                val response = remoteDataSource.searchMeals(query)
                Result.success(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMealDetail(mealId: String): Result<MealDetailResponse> {
        return try {
            val response = remoteDataSource.getMealDetail(mealId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun filterByCategory(category: String): Result<MealResponse> {
        return try {
            val response = remoteDataSource.filterByCategory(category)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun filterByArea(area: String): Result<MealResponse> {
        return try {
            val response = remoteDataSource.filterByArea(area)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun filterByIngredient(ingredient: String): Result<MealResponse> {
        return try {
            val response = remoteDataSource.filterByIngredient(ingredient)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAllCategories(): Result<List<String>> {
        return try {
            val response = remoteDataSource.getAllCategories()
            val categories = response.categories?.map { it.name } ?: emptyList()
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAllAreas(): Result<List<String>> {
        return try {
            val response = remoteDataSource.getAllAreas()
            val areas = response.areas?.map { it.name } ?: emptyList()
            Result.success(areas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAllIngredients(): Result<List<String>> {
        return try {
            val response = remoteDataSource.getAllIngredients()
            val ingredients = response.ingredients?.map { it.name } ?: emptyList()
            Result.success(ingredients)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getAllFavorites(): Flow<List<MealDetail>> {
        val ingredientListType: Type = object : TypeToken<List<Ingredient>>() {}.type
        return localDataSource.getAllFavorites().map { entities ->
            entities.map { entity ->
                val ingredients: List<Ingredient> = gson.fromJson(entity.ingredientsJson, ingredientListType) ?: emptyList()
                MealDetail(
                    id = entity.mealId,
                    name = entity.name,
                    imageUrl = entity.imageUrl,
                    instructions = entity.instructions,
                    ingredients = ingredients
                )
            }
        }
    }
    
    override suspend fun getFavoriteById(mealId: String): MealDetail? {
        val entity = localDataSource.getFavoriteById(mealId) ?: return null
        val ingredientListType: Type = object : TypeToken<List<Ingredient>>() {}.type
        val ingredients: List<Ingredient> = gson.fromJson(entity.ingredientsJson, ingredientListType) ?: emptyList()
        return MealDetail(
            id = entity.mealId,
            name = entity.name,
            imageUrl = entity.imageUrl,
            instructions = entity.instructions,
            ingredients = ingredients
        )
    }
    
    override fun isFavorite(mealId: String): Flow<Boolean> {
        return localDataSource.isFavorite(mealId)
    }
    
    override suspend fun addToFavorites(meal: MealDetail) {
        localDataSource.saveFavorite(meal)
    }
    
    override suspend fun removeFromFavorites(mealId: String) {
        localDataSource.removeFavorite(mealId)
    }
    
    override suspend fun clearAllFavorites() {
        localDataSource.clearAllFavorites()
    }
}
