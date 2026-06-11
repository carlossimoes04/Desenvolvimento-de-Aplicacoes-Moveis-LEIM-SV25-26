package dam_A51696.pantrychef.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
 * Componente Composable que renderiza o ecrã de registo (Sign Up) da aplicação
 *
 * Apresenta campos de introdução para e-mail, palavra-passe e confirmação de palavra-passe,
 * gerindo a criação de novas contas em articulação com a [AuthViewModel]
 *
 * @param viewModel A instância de [AuthViewModel] que gere as operações de autenticação
 * @param onNavigateToLogin Callback invocado para navegar de volta ao ecrã de início de sessão
 * @param onSignUpSuccess Callback invocado quando o registo de conta é efetuado com sucesso
 */
@Composable
fun SignUpScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    // subscreve e observa as alterações do estado de autenticação vindas da viewmodel
    val authState by viewModel.authState.collectAsState()

    // guarda o estado mutável do e-mail introduzido pelo utilizador
    var email by remember { mutableStateOf("") }
    // guarda o estado mutável da palavra-passe de registo
    var password by remember { mutableStateOf("") }
    // guarda o estado mutável da confirmação de palavra-passe
    var passwordConfirm by remember { mutableStateOf("") }

    // reage a alterações no estado de autenticação para navegar em caso de sucesso
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            // se o registo for bem-sucedido, navega para o ecrã seguinte e limpa o
            // estado da viewmodel
            onSignUpSuccess()
            viewModel.resetState()
        }
    }

    // contentor principal que centra vertical e horizontalmente o conteúdo no ecrã
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground),
        contentAlignment = Alignment.Center
    ) {
        // alinha os campos do formulário e textos verticalmente
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // título do ecrã de registo
            Text(
                text = "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            )
            // subtítulo de convite ao utilizador
            Text(
                text = "Join Pantry Chef today!",
                fontSize = 16.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            // campo para introduzir o e-mail com ícone ilustrativo à esquerda
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

            // campo para introduzir a palavra-passe com ocultação de caracteres para privacidade
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

            Spacer(modifier = Modifier.height(16.dp))

            // campo para confirmar a palavra-passe com as mesmas características de segurança
            OutlinedTextField(
                value = passwordConfirm,
                onValueChange = { passwordConfirm = it },
                label = { Text("Confirm Password") },
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

            // mostra a mensagem de erro a vermelho caso a validação ou o registo falhem
            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = (authState as AuthState.Error).message, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // botão para submeter o pedido de registo de conta
            Button(
                onClick = { viewModel.signUp(email, password, passwordConfirm) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(16.dp),
                enabled = authState !is AuthState.Loading
            ) {
                // apresenta o indicador de progresso circular se estiver a carregar,
                // caso contrário exibe o texto
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign Up", color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // linha inferior para encaminhar utilizadores que já têm conta
            // de volta para o login
            Row {
                Text("Already have an account? ", color = Color.Gray)
                Text(
                    text = "Sign In",
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

/**
 * Desenvolvi este ecrã com o intuito de permitir a novos utilizadores
 * criarem de forma simples e intuitiva uma conta de acesso na aplicação Pantry Chef
 *
 * Decisões de Implementação:
 * - LaunchedEffect:
 *      Utilizei este efeito para detetar quando o estado de autenticação passa
 *      a Success, garantindo que a transição de ecrã e o reset do estado da ViewModel
 *      ocorrem apenas uma única vez
 * - passwordConfirm:
 *      Adicionei um campo dedicado à confirmação da palavra-passe para que o
 *      utilizador possa validar o seu input localmente na UI antes de efetuar o registo
 * - OutlinedTextFields:
 *      Desenhei os campos com cantos arredondados (RoundedCornerShape) e cores de fundo
 *      que contrastam com o fundo creme do ecrã, garantindo foco visual através do
 *      indicador verde
 * - Controlo de Estados Assíncronos:
 *      Desativei o clique no botão de registo quando a operação está pendente (Loading),
 *      exibindo temporariamente um indicador de progresso circular para otimizar o fluxo
 *      e evitar cliques repetidos
 */