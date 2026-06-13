package dam_A51696.pantrychef.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dam_A51696.pantrychef.core.utils.Constants
import dam_A51696.pantrychef.data.remote.api.MealDbApi
import dam_A51696.pantrychef.data.remote.api.NvidiaApi
import dam_A51696.pantrychef.data.repository.PantryRepositoryImpl
import dam_A51696.pantrychef.data.repository.RecipeRepositoryImpl
import dam_A51696.pantrychef.domain.repository.PantryRepository
import dam_A51696.pantrychef.domain.repository.RecipeRepository
import dam_A51696.pantrychef.domain.repository.ShoppingRepository
import dam_A51696.pantrychef.data.repository.ShoppingRepositoryImpl
import dam_A51696.pantrychef.domain.repository.FavoriteRepository
import dam_A51696.pantrychef.data.repository.FavoriteRepositoryImpl
import dam_A51696.pantrychef.domain.repository.AuthRepository
import dam_A51696.pantrychef.data.repository.AuthRepositoryImpl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo do Hilt para fornecer dependências globais à aplicação
 *
 * Está instalado no [SingletonComponent], o que garante que as dependências
 * vivem durante todo a sessão da aplicação
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Configura e fornece o cliente [Retrofit] para realizar
     * pedidos HTTP à API de receitas
     *
     * @return Cliente [Retrofit] configurado
     */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        // constrói o cliente Retrofit com o URL e o conversor Gson
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Fornece a interface [MealDbApi] criada pelo Retrofit para
     * aceder aos endpoints de receitas
     *
     * @param retrofit Instância do [Retrofit] necessária para criar a interface
     * @return Implementação de [MealDbApi]
     */
    @Provides
    @Singleton
    fun provideMealDbApi(retrofit: Retrofit): MealDbApi {
        // gera a implementação da interface da API a partir do Retrofit
        return retrofit.create(MealDbApi::class.java)
    }

    /**
     * Fornece a implementação do repositório da despensa
     *
     * @return Instância de [PantryRepository]
     */
    @Provides
    @Singleton
    fun providePantryRepository(): PantryRepository {
        return PantryRepositoryImpl()
    }

    /**
     * Fornece a implementação do repositório de receitas
     *
     * @param api Instância da API de rede
     * @return Instância de [RecipeRepository]
     */
    @Provides
    @Singleton
    fun provideRecipeRepository(api: MealDbApi): RecipeRepository {
        return RecipeRepositoryImpl(api)
    }

    /**
     * Fornece a implementação do repositório da lista de compras
     *
     * @return Instância de [ShoppingRepository]
     */
    @Provides
    @Singleton
    fun provideShoppingRepository(): ShoppingRepository {
        return ShoppingRepositoryImpl()
    }

    /**
     * Fornece a implementação do repositório de favoritos
     *
     * @return Instância de [FavoriteRepository]
     */
    @Provides
    @Singleton
    fun provideFavoriteRepository(): FavoriteRepository {
        return FavoriteRepositoryImpl()
    }

    /**
     * Fornece a implementação do repositório de autenticação
     *
     * @return Instância de [AuthRepository]
     */
    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }

    /**
     * Fornece a interface da Nvidia API com um cliente OkHttp customizado
     * para aguentar chamadas longas (60 segundos) do modelo Llama.
     */
    @Provides
    @Singleton
    fun provideNvidiaApi(): NvidiaApi {
        // cliente HTTP personalizado com timeout
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS) // tempo limite de leitura
            .connectTimeout(60, TimeUnit.SECONDS) // tempo limite de conexão
            .build() // constrói o cliente
        // instancia da API com o cliente personalizado
        return Retrofit.Builder()
            .baseUrl("https://integrate.api.nvidia.com/v1/") // URL base da API
            .client(okHttpClient) // usa o cliente personalizado
            .addConverterFactory(GsonConverterFactory.create()) // conversor Gson
            .build() // constrói a instância
            .create(NvidiaApi::class.java) // cria a implementação da API
    }
}

/*
 * Criei este módulo para centrar a criação das dependências da aplicação
 *
 * Utilizo a anotação @Provides para registar estas funções no Hilt,
 * de modo que a biblioteca saiba que classe deve criar sempre que a
 * aplicação pedir uma interface no construtor dos ViewModels
 *
 * Utilizo a anotação @Singleton para garantir que existe apenas um objeto de
 * cada tipo em toda a aplicação, o que poupa recursos e evita criar conexões ou
 * clientes HTTP em duplicado durante o tempo de execução
 */