package dam.exer_1_1

/**
 * A sealed class Event serve para definir os eventos possíveis no sistema.
 *
 * Por ser sealed, o compilador garante exaustividade em expressões [when],
 * tornando impossível a existência de subtipos não tratados.
 */
sealed class Event {
    /*
     Uma sealed class é uma classe que restringe a hierarquia de herança,
     permitindo apenas que as subclasses definidas no mesmo ficheiro ou
     bloco a estendam. Isto garante que o compilador conhece todos os
     subtipos possíveis, tornando expressões when exaustivas e seguras em
     tempo de compilação.

     Uma data class é uma classe cujo propósito principal é guardar dados.
     O compilador gera automaticamente os métodos equals(), hashCode(),
     toString() e copy(), com base nas propriedades declaradas no construtor
     primário.
     */

    /**
     * Regista a entrada de um utilizador no sistema.
     *
     * @property username Nome do utilizador. Identificador único do mesmo.
     * @property timestamp Instante do evento, em milissegundos.
     */
    data class Login(val username: String, val timestamp: Long) : Event()

    /**
     * Regista uma compra efetuada por um utilizador.
     *
     * @property username Nome do utilizador. Identificador único do mesmo.
     * @property amount Valor da compra, em euros (não negativo).
     * @property timestamp Instante do evento, em milissegundos.
     */
    data class Purchase(val username: String, val amount: Double, val timestamp: Long) : Event()

    /**
     * Regista a saída de um utilizador do sistema.
     *
     * @property username Nome do utilizador. Identificador único do mesmo.
     * @property timestamp Instante do evento, em milissegundos.
     */
    data class Logout(val username: String, val timestamp: Long) : Event()
}

/**
 * Extensão de List<Event> que filtra os eventos associados a um utilizador.
 *
 * @param username Identificador do utilizador cujos eventos se pretende obter.
 * @return Nova lista contendo apenas os eventos do utilizador indicado.
 */
fun List<Event>.filterByUser(username: String) : List<Event> {
    // this refere-se à lista original sobre a qual a extensão é chamada
    return this.filter { event -> // filter retorna uma lista com os elementos onde a condição é true
        when (event) {
            is Event.Login -> event.username == username // compara o username do evento Login
            is Event.Purchase -> event.username == username // compara o username do evento Purchase
            is Event.Logout -> event.username == username // compara o username do evento Logout
        }
    }
}

// val -> não pode ser alterado | var -> pode ser alterada

/**
 * Calcula o total gasto por um utilizador específico.
 *
 * Extensão de [List]<[Event]> que filtra os eventos do utilizador
 * e soma os valores de todas as suas compras ([Event.Purchase]).
 *
 * @param username Identificador do utilizador cujo total se pretende calcular.
 * @return Soma de todos os valores de compras do utilizador. Devolve 0.0 se não houver compras.
 */
fun List<Event>.totalSpent(username: String) : Double {
    // reutiliza a extensão filterByUser para obter apenas os eventos do utilizador
    val userEvents = filterByUser(username)
    // filterIsInstance<Event.Purchase>() filtra a lista mantendo apenas os elementos
    // que são instâncias de Event.Purchase, ignorando Login e Logout
    // sumOf itera sobre esses elementos e acumula a soma da propriedade amount
    val total = userEvents.filterIsInstance<Event.Purchase>().sumOf { compra -> compra.amount }
    return total // devolve a soma total das compras do utilizador
}

/**
 * Função de ordem superior que aplica um handler a cada evento da lista.
 *
 * @param list Lista de eventos a processar.
 * @param handler Lambda que define a ação a executar sobre cada evento. Recebe um [Event] e não devolve nada (Unit).
 */
fun processEvents(list: List<Event>, handler: (Event) -> Unit) {
    // forEach itera sobre cada elemento da lista, passando-o ao handler
    // processEvents é de ordem superior porque recebe o handler
    // é passada como argumento, permitindo que o comportamento
    // seja definido pelo chamador da função
    list.forEach { e -> handler(e) } // invoca o handler para cada evento "e"
}

fun main () {
    val events = listOf(
        Event.Login("alice", 1_000),
        Event.Purchase("alice", 49.99, 1_100),
        Event.Purchase("bob", 19.99, 1_200),
        Event.Login("bob", 1_050),
        Event.Purchase("alice", 15.00, 1_300),
        Event.Logout("alice", 1_400),
        Event.Logout("bob", 1_500)
    )

    processEvents(events) { event ->
        // o handler definido em processEvents começa aqui
        when (event) {
            is Event.Login -> println("[LOGIN] ${event.username} logged in at t=${event.timestamp}")
            is Event.Purchase -> println("[PURCHASE] ${event.username} spent $${event.amount} at t=${event.timestamp}")
            is Event.Logout -> println("[LOGOUT] ${event.username} logged out at t=${event.timestamp}")
        }
    }

    println("Total spent by alice: $${events.totalSpent("alice")}")
    println("Total spent by bob: $${events.totalSpent("bob")}")

    println("Events for alice:")
    val aliceEvents = events.filterByUser("alice")
    aliceEvents.forEach { event ->
        println(event)
    }

    /*
    como os events for alice apareciam como:
    dam.exer_1_1.Event$Login@5f184fc6
    dam.exer_1_1.Event$Purchase@3feba861
    dam.exer_1_1.Event$Purchase@5b480cf9
    dam.exer_1_1.Event$Logout@6f496d9f
    então alteraram-se as subclasses de class para data class
    */
}