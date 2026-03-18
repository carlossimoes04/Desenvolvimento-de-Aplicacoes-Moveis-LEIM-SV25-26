package dam.exer_1_1

sealed class Event {
    class Login(val username: String, val timestamp: Long) : Event()
    class Purchase(val username: String, val amount: Double, val timestamp: Long) : Event()
    class Logout(val username: String, val timestamp: Long) : Event()
}

fun List<Event>.filterByUser(username: String) : List<Event> {
    return this.filter { event -> // itera sobre a List<Event> original, e só mantém na nova lista
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