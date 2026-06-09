package dam_A51696.pantrychef.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dam_A51696.pantrychef.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementação do [AuthRepository] que utiliza o
 * Firebase Authentication para gerir a autenticação
 * dos utilizadores
 */
class AuthRepositoryImpl : AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    /**
     * Flow que emite o utilizador atual sempre que o estado de autenticação muda
     *
     * Utiliza [callbackFlow] para converter o listener do Firebase num Flow de Kotlin,
     * e remove o listener quando o Flow deixa de ser observado através do [awaitClose]
     */
    override val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    /**
     * Faz login com email e password
     *
     * @param email Email do utilizador
     * @param password Password do utilizador
     * @return [Result.success] se o login correr bem, [Result.failure] com a exceção caso contrário
     */
    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Regista um novo utilizador com email e password
     *
     * @param email Email do utilizador
     * @param password Password do utilizador
     * @return [Result.success] se o registo correr bem, [Result.failure] com a exceção caso contrário
     */
    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Faz logout do utilizador atual
     */
    override suspend fun logout() {
        auth.signOut()
    }

    /**
     * Devolve o utilizador atual de forma síncrona
     *
     * @return O [FirebaseUser] atual ou null se não houver ninguém autenticado
     */
    override fun getCurrentUserSync(): FirebaseUser? {
        return auth.currentUser
    }
}

/*
 * Esta classe é a implementação concreta do AuthRepository e é aqui que é utilizado o Firebase
 * Authentication para fazer login, registo e logout
 *
 * Para o currentUser usei callbackFlow porque o Firebase usa listeners (callbacks)
 * para notificar mudanças de autenticação, e eu preciso de converter isso num Flow para o
 * resto da aplicação poder observar de forma reativa
 *
 * As funções login e signUp devolvem Result porque assim quem chama estas funções consegue
 * tratar o sucesso e o erro sem precisar de try-catch
 */