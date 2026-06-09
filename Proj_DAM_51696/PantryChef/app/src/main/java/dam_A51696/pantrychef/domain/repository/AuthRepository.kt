package dam_A51696.pantrychef.domain.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define as operações de autenticação da aplicação
 *
 * Serve como contrato para a camada de apresentação
 * interagir com a autenticação
 */
interface AuthRepository {

    /**
     * Fluxo que emite o utilizador atual sempre que o estado de autenticação muda
     */
    val currentUser: Flow<FirebaseUser?>

    /**
     * Realiza o login na aplicação com email e password
     *
     * É suspend porque é uma operação assíncrona
     *
     * @param email Email do utilizador
     * @param password Password do utilizador
     * @return [Result] indicando o sucesso ou a falha da operação
     */
    suspend fun login(email: String, password: String): Result<Unit>

    /**
     * Regista um novo utilizador com email e password
     *
     * @param email Email do utilizador
     * @param password Password do utilizador
     * @return [Result] indicando o sucesso ou a falha da operação
     */
    suspend fun signUp(email: String, password: String): Result<Unit>

    /**
     * Faz logout do utilizador atual
     */
    suspend fun logout()

    /**
     * Devolve o utilizador atual de forma síncrona
     *
     * @return O [FirebaseUser] atual ou null se não houver ninguém autenticado
     */
    fun getCurrentUserSync(): FirebaseUser?
}

/*
 * Criei esta interface na camada de domínio para definir as operações de autenticação sem ligar
 * o código ao Firebase
 *
 * Esta separação permite que os ViewModels dependam apenas das assinaturas, o que evita que o
 * domínio dependa da camada de dados e facilita a substituição de tecnologias ou a criação de
 * testes com mocks
 *
 * Utilizei Flow para expor o utilizador porque o estado da sessão sofre alterações ao longo do
 * tempo e a aplicação precisa de receber atualizações sempre que há login ou logout
 *
 * Não usei StateFlow ou SharedFlow na interface porque estes são fluxos com gestão de cache
 * e de ciclo de vida, o que deve ser tratado nos ViewModels e não pelo repositório, pelo que
 * esta interface expõe apenas o Flow e deixa a forma de consumo para quem chama as funções
 *
 * ? - FirebaseUser pode ser nulo se não houver login
 */