package dam.exer_1_1

sealed class Event {
    data class Login(val username: String, val timestamp: Long) : Event()
    data class Purchase(val username: String, val amount: Double, val timestamp: Long) : Event()
    data class Logout(val username: String, val timestamp: Long) : Event()
}

fun List<Event>.filterByUser(username: String) : List<Event> {
    return this.filter { event ->
        // itera sobre a List<Event> original, e só mantém na nova lista
        // os elementos onde a condição retorna true
        when (event) { // vai procurar o username dependendo do evento
            is Event.Login -> event.username == username
            is Event.Purchase -> event.username == username
            is Event.Logout -> event.username == username
        }
        // com este when, é possível retornar apenas os eventos filtrados com o nome de utilizador
        // comparando com as propriedades de cada subclasse
    }
}

// val -> não pode ser alterado | var -> pode ser alterada
fun List<Event>.totalSpent(username: String) : Double {
    val userEvents = filterByUser(username)
    val total = userEvents.filterIsInstance<Event.Purchase>().sumOf { compra -> compra.amount }
    return total
}

fun processEvents(list: List<Event>, handler: (Event) -> Unit) {
    list.forEach { e -> handler(e) }
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