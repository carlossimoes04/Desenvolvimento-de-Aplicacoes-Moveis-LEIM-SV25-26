package dam_A51696.pantrychef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dam_A51696.pantrychef.presentation.navigation.AppNavigation
import dam_A51696.pantrychef.presentation.theme.PantryChefTheme


/**
 * Ponto de entrada principal da UI da aplicação
 *
 * Esta classe herda de [ComponentActivity], que é a classe base recomendada
 * para aplicações que utilizam o Jetpack Compose
 *
 * A anotação [AndroidEntryPoint] é crucial: ela indica ao Dagger Hilt que
 * esta Activity é um ponto de entrada para injeção de dependências. Isto
 * permite que dependências (como os ViewModels do Hilt) sejam fornecidas
 * aos ecrãs Compose que são lançados a partir daqui
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Chamado quando a Activity está a ser iniciada
     *
     * Este método é responsável por:
     * - Inicializar a Activity base;
     * - Ativar o modo `EdgeToEdge`, permitindo que o conteúdo da app
     *    seja desenhado atrás das barras de sistema (barra de estado em cima
     *    e barra de navegação em baixo), para um design mais imersivo;
     * - Definir o conteúdo visual da Activity usando o [setContent] do Compose,
     *    onde é aplicado o tema global [PantryChefTheme] e iniciada a árvore
     *    de navegação da aplicação com [AppNavigation];
     *
     * @param savedInstanceState Se a Activity estiver a ser reiniciada após
     * ter sido encerrada, este Bundle contém os dados mais recentes fornecidos.
     * Caso contrário, é null
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PantryChefTheme {
                AppNavigation()
            }
        }
    }
}