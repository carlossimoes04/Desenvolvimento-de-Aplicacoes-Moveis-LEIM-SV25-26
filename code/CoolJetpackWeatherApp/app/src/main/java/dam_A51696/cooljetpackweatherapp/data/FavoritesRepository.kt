package dam_A51696.cooljetpackweatherapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dam_A51696.cooljetpackweatherapp.ui.FavoriteLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// cria uma extensão sobre Context inicializar e aceder a uma instância do DataStore chamada "favorites"
// responsável por guardar a lista de locais favoritos
private val Context.dataStore by preferencesDataStore(name = "favorites")

object FavoritesRepository { // sendo object, garante que só existe uma instância de FavoritesRepository a gerir os favoritos em toda a aplicação

    // configura a ferramenta JSON
    // a opção 'ignoreUnknownKeys' previne erros caso a estrutura dos dados guardados tenha propriedades desconhecidas
    private val json = Json { ignoreUnknownKeys = true }

    // cria uma chave única (para texto/string) que serve de identificador para guardar e ler os dados no DataStore
    private val FAVORITES_KEY = stringPreferencesKey("favorites_list")

    // função assíncrona (suspend) para não bloquear a interface principal da aplicação durante a gravação dos dados
    // recebe o contexto e a lista a gravar
    suspend fun saveFavorites(context: Context, favorites: List<FavoriteLocation>) {
        val encoded = json.encodeToString(favorites) // converte de List para JSON para o DataStore conseguir armazenar
        context.dataStore.edit { prefs -> // DataStore em modo de edição permite alterar os valores guardados
            prefs[FAVORITES_KEY] = encoded // associa a string gerada com os favoritos a "favorites_list" e guarda o valor
        }
    }

    // função que recupera os dados
    // devolve um Flow, que é um canal de dados observável, permitindo que a interface reaja automaticamente quando os dados mudam
    fun loadFavorites(context: Context): Flow<List<FavoriteLocation>> {
        return context.dataStore.data.map { prefs -> // acede aos dados do DataStore e utiliza o 'map' para transformar as preferências numa lista estruturada
            val raw = prefs[FAVORITES_KEY] ?: return@map emptyList()
            // obtém a string JSON através da chave: se o valor for nulo (primeira vez que a aplicação abre),
            // interrompe a transformação e devolve uma lista vazia
            json.decodeFromString(raw) // pega na string JSON recuperada e reconstrói a lista original de objetos FavoriteLocation
        }
    }
}