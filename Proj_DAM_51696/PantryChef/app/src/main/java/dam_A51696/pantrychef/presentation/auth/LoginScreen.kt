package dam_A51696.pantrychef.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dam_A51696.pantrychef.presentation.theme.CreamBackground
import dam_A51696.pantrychef.presentation.theme.ForestGreen
import dam_A51696.pantrychef.presentation.theme.PrimaryOrange
import dam_A51696.pantrychef.presentation.theme.White

/**
 * Componente Composable que renderiza o ecrã de início de sessão (Login) da aplicação
 *
 * Apresenta o formulário de credenciais composto por campos para o e-mail e palavra-passe,
 * reagindo dinamicamente às alterações de estado provenientes da [AuthViewModel]
 *
 * @param viewModel A instância de [AuthViewModel] que gere as operações de autenticação
 * @param onNavigateToSignUp Callback invocado para navegar para o ecrã de registo de conta
 * @param onLoginSuccess Callback invocado quando o login é efetuado com sucesso
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit, // unit é o equivalente ao void em Java
    onLoginSuccess: () -> Unit
) {
    // subscreve e observa as alterações do estado de autenticação da viewmodel
    val authState by viewModel.authState.collectAsState()

    // guarda o estado mutável do e-mail introduzido pelo utilizador
    var email by remember { mutableStateOf("") }
    // guarda o estado mutável da palavra-passe introduzida
    var password by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current // para esconder o teclado

    // by -> serve para não precisar usar .value
    // remember -> evita que os dados sejam perdidos quando o ecrã atualiza

    // reage de forma segura a alterações no estado de autenticação durante o ciclo de vida
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            // executa a navegação de sucesso e repõe o estado da viewmodel
            // para evitar reexecuções
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    /*
    O LaunchedEffect é uma função "Composable" projetada para executar operações assíncronas
    (como chamadas de rede ou temporizadores) com segurança

    Este inicia uma Coroutine associada ao ciclo de vida do componente e cancela a tarefa
    automaticamente quando esse componente sai da tela
     */

    // contentor principal que centra o conteúdo do ecrã de login
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .displayCutoutPadding()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
        contentAlignment = Alignment.Center
    ) {
        // organiza os elementos verticalmente com espaçamento lateral
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // título principal da aplicação com estilo personalizado
            Text(
                text = "Pantry Chef",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            // mensagem de boas-vindas secundária
            Text(
                text = "Welcome back!",
                fontSize = 16.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            // campo de introdução de e-mail com ícone ilustrativo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email,
                    contentDescription = null, tint = ForestGreen) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedIndicatorColor = ForestGreen,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // campo de introdução de palavra-passe com máscara visual para segurança
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock,
                    contentDescription = null, tint = ForestGreen) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedIndicatorColor = ForestGreen,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // exibe dinamicamente uma mensagem de erro caso o estado de autenticação falhe
            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = (authState as AuthState.Error).message, color = Color.Red,
                    fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // botão para submeter as credenciais de login
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(16.dp),
                enabled = authState !is AuthState.Loading // desativa o botão se estiver a carregar

                // !is -> serve para verificar se um objeto não pertence a um determinado
                // tipo ou claase
            ) {
                // mostra um indicador de progresso circular se estiver a carregar,
                // caso contrário exibe o texto
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign In", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // linha inferior contendo a opção para navegar para o ecrã de registo
            Row {
                Text("Don't have an account? ", color = Color.Gray)
                Text(
                    text = "Sign Up",
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToSignUp() }
                )
            }
        }
    }
}

/**
 * Desenvolvi este ecrã para servir de interface visual principal no início de sessão
 * da app Pantry Chef
 *
 * Decisões de Implementação:
 * - LaunchedEffect:
 *      Garante a observação segura do estado de sucesso no ciclo de vida do Compose,
 *      prevenindo navegações repetidas ou ciclos infinitos
 * - OutlinedTextField:
 *      Escolhi campos arredondados com cores neutras para um aspeto limpo e moderno,
 *      usando máscaras para a palavra-passe e configurações de teclado adequadas para e-mail
 * - Cores e Tema:
 *      Segui a paleta definida no design da app (CreamBackground, ForestGreen, PrimaryOrange)
 *      para assegurar consistência visual
 * - Reatividade do Botão:
 *      Desativei o botão durante operações assíncronas para evitar cliques duplos e
 *      exibi um CircularProgressIndicator como feedback visual de processamento
 */