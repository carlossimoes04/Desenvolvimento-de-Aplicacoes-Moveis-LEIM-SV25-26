package dam_A51696.pantrychef.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dam_A51696.pantrychef.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Representa os diferentes estados possíveis do fluxo de autenticação na UI
 *
 * É uma Sealed Class que serve como uma máquina de estados segura para
 * o ecrã de login/registo
 */
sealed class AuthState {
    /**
     * Estado inicial ou de repouso: onde nenhuma operação de autenticação está ativa
     */
    object Idle : AuthState()
    /**
     * Estado de carregamento: ativado enquanto aguarda a resposta do repositório
     * (chamada de rede/API)
     */
    object Loading : AuthState()
    /**
     * Estado de sucesso: indicando que a operação (login ou registo)
     * foi concluída com êxito
     */
    object Success : AuthState()
    /**
     * Estado de erro: indicando que a operação falhou e que
     * contém a mensagem detalhada do erro
     *
     * @property message Mensagem descritiva do erro para exibir ao utilizador na UI
     */
    data class Error(val message: String) : AuthState()
}

/**
 * ViewModel responsável por gerir o estado da autenticação e expor as ações necessárias
 * (login, registo e logout) à UI, fazendo a ponte com o repositório de autenticação
 *
 * Utiliza o Hilt para injeção de dependências através da anotação `@HiltViewModel`
 *
 * @property authRepository Repositório encarregue de processar a autenticação
 * (ex: Firebase ou base de dados)
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * Fluxo interno mutável (MutableStateFlow) que guarda o estado atual
     * de autenticação
     *
     * É privado para evitar que a UI possa alterar diretamente o
     * estado sem passar pela ViewModel
     */
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)

    /**
     * Fluxo de estado público e apenas de leitura (StateFlow) exposto para a UI
     *
     * Os componentes gráficos devem subscrever este fluxo para reagir
     * às mudanças de estado
     */
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Fluxo que disponibiliza os dados do utilizador atualmente
     * autenticado a partir do repositório
     *
     * Permite à UI monitorizar se existe um utilizador com sessão iniciada
     */
    val currentUser = authRepository.currentUser

    /**
     * Efetua a tentativa de login de um utilizador utilizando
     * o seu e-mail e palavra-passe
     *
     * @param email O e-mail inserido no formulário de login
     * @param pass A palavra-passe inserida no formulário de login
     */
    fun login(email: String, pass: String) {
        // se o email e a palavra passe estiverem vazios
        if (email.isBlank() || pass.isBlank()) {
            // avisa que é necessário preencher todos os campos
            _authState.value = AuthState.Error("Preenche todos os campos.")
            // não efetua o login | interrompe a execução
            return
        }
        // inicia uma corrotina para efetuar o login de forma segura e
        // não bloquear a main thread
        viewModelScope.launch {
            // altera o estado para loading para feedback visual na UI
            _authState.value = AuthState.Loading
            // efetua o login no repositório
            val result = authRepository.login(email, pass)
            // se o login foi bem-sucedido
            if (result.isSuccess) {
                // altera o estado para sucesso
                _authState.value = AuthState.Success
            } else { // se não foi bem-sucedido
                // envia um estado de erro com a mensagem de erro
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Erro no Login")
            }
        }
    }

    /**
     * Efetua o registo de uma nova conta de utilizador
     *
     * @param email O e-mail do novo utilizador
     * @param pass A palavra-passe definida pelo utilizador
     * @param passConfirm A confirmação da palavra-passe, que tem de ser igual à original
     */
    fun signUp(email: String, pass: String, passConfirm: String) {
        // validação local: verifica se algum dos campos está vazio
        if (email.isBlank() || pass.isBlank() || passConfirm.isBlank()) {
            _authState.value = AuthState.Error("Preenche todos os campos.")
            return
        }
        // validação local: verifica se as passwords coincidem
        if (pass != passConfirm) {
            _authState.value = AuthState.Error("As passwords não coincidem.")
            return
        }
        // inicia uma corrotina para efetuar o registo de forma segura e
        // não bloquear a main thread
        viewModelScope.launch {
            // altera o estado para loading para feedback visual na UI
            _authState.value = AuthState.Loading
            // efetua o registo no repositório
            val result = authRepository.signUp(email, pass)
            if (result.isSuccess) { // se o registo foi bem-sucedido
                _authState.value = AuthState.Success // altera o estado para sucesso
            } else { // se não foi bem-sucedido
                // envia um estado de erro com a mensagem de erro
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Erro no Registo")
            }
        }
    }

    /**
     * Termina a sessão do utilizador atualmente autenticado
     */
    fun logout() {
        // inicia uma corrotina para terminar a sessão de forma segura e s
        // em bloquear a main thread
        viewModelScope.launch {
            authRepository.logout() // efetua o logout no repositório
            _authState.value = AuthState.Idle // repõe o estado de autenticação do ViewModel para idle
        }
    }

    /**
     * Limpa o estado atual de autenticação, repondo-o para [AuthState.Idle]
     *
     * Útil para dar reset a erros ou sucessos anteriores ao navegar entre
     * ecrãs, ou abrir caixas de diálogo
     */
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

/**
 * Criei esta ViewModel para gerir o estado de autenticação de forma isolada da UI,
 * respeitando o padrão MVVM
 *
 * Decisões de Implementação:
 * - AuthState (Sealed Class):
 *      Modela de forma segura os estados do fluxo (Idle, Loading, Success, Error)
 * - Encapsulamento de Fluxo:
 *      Protege o estado com um _authState privado mutável e um authState público
 *      imutável para a UI subscrever
 * - Injeção via Construtor (Hilt):
 *      Reduz a dependência ao injetar a interface AuthRepository, facilitando
 *      testes e futuras mudanças de banco de dados
 * - Validações Locais:
 *      Verifica strings vazias e passwords não correspondentes localmente para
 *      evitar requisições à API com pedidos desnecessários
 * - viewModelScope:
 *      Corre todas as tarefas assíncronas do repositório em corrotinas Kotlin,
 *      prevenindo memory leaks, se a View for destruída, e bloqueios da main thread
 * - resetState():
 *      Permite à UI repor o estado para Idle para não arrastar erros anteriores
 *      de um ecrã para o outro
 */