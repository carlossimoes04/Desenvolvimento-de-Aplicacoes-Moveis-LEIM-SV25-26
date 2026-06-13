package dam_A51696.pantrychef.data.remote.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Modelo que representa uma mensagem individual na conversa com a IA
 *
 * @param role autor da mensagem (ex: "system", "user" ou "assistant")
 * @param content texto escrito na mensagem
 */
data class ChatMessage(
    val role: String,
    val content: String
)

/**
 * Pedido enviado para a API da Nvidia com o contexto da conversa e as regras
 *
 * @param model nome do modelo de IA a utilizar
 * @param messages lista com o histórico de mensagens a enviar
 * @param temperature grau de criatividade da resposta (menor = mais conservador e exato)
 * @param top_p probabilidade cumulativa das palavras para controlo de coerência
 * @param max_tokens limite de tamanho da resposta gerada
 * @param stream define se a resposta chega aos poucos ou toda de uma vez
 */
data class ChatRequest(
    // modelo focado em seguir instruções complexas definido por defeito
    val model: String = "meta/llama-3.3-70b-instruct",
    val messages: List<ChatMessage>,
    val temperature: Double = 0.2,
    val top_p: Double = 0.7,
    val max_tokens: Int = 1024,
    val stream: Boolean = false
)


/**
 * Representa uma das opções de resposta devolvidas pelo modelo
 *
 * @param message a mensagem gerada que contém a resposta final da IA
 */
data class ChatChoice(
    val message: ChatMessage
)


/**
 * Estrutura principal da resposta recebida da API da Nvidia
 *
 * @param choices lista com as respostas geradas (por norma devolve apenas uma opção)
 */
data class ChatResponse(
    val choices: List<ChatChoice>
)

/**
 * Interface do Retrofit para comunicação remota com a API da Nvidia NIM
 *
 * Define o contrato e a rota HTTP necessária para solicitar traduções
 * processadas por inteligência artificial na cloud
 */
interface NvidiaApi {
    /**
     * Envia o texto da receita e obtém a versão traduzida
     *
     * @param authHeader cabeçalho de segurança com a chave da API no formato Bearer Token
     * @param request objeto de pedido com o modelo e a lista de mensagens a processar
     * @return objeto [ChatResponse] contendo a resposta traduzida pelo modelo
     */
    // define que os dados enviados são em formato json
    @Headers("Content-Type: application/json")
    // aponta para o endpoint normalizado de conversação
    @POST("chat/completions")
    suspend fun translateRecipe(
        @retrofit2.http.Header("Authorization") authHeader: String,
        @Body request: ChatRequest
    ): ChatResponse
}

/*
 * Criei este ficheiro para isolar a comunicação entre a aplicação e a IA
 *
 * Como a API da Nvidia NIM é compatível com a estrutura de mensagens da OpenAI,
 * construí as classes de dados com base nos padrões estabelecidos de chat completions
 *
 * Classes e funções implementadas:
 * - ChatMessage, ChatRequest, ChatChoice e ChatResponse:
 *      Classes de dados utilizadas como DTOs (Data Transfer Objects). O Gson
 *      do Retrofit mapeia estas variáveis para o formato JSON correto que a API
 *      espera receber e ler
 * - NvidiaApi:
 *      Interface colocada na camada de dados para abstrair os detalhes do protocolo HTTP.
 *      Possui o endpoint POST que despacha o texto para tradução e exige o cabeçalho
 *      de autorização onde a app injeta a chave segura gerada no build
 */