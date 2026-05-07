package tasks

import contributors.User

/*
TODO: Write aggregation code.

 In the initial list each user is present several times, once for each
 repository he or she contributed to.
 Merge duplications: each user should be present only once in the resulting list
 with the total value of contributions for all the repositories.
 Users should be sorted in a descending order by their contributions.

 The corresponding test can be found in test/tasks/AggregationKtTest.kt.
 You can use 'Navigate | Test' menu action (note the shortcut) to navigate to the test.

 Implement the aggregate() function combining the users so that each
 contributor is added only once.
 The User.contributions property should contain the total number of contributions
 of the given user to all the projects.
 The resulting list should be sorted in descending order according
 to the number of contributions
*/
fun List<User>.aggregate(): List<User> {
    val userMap = mutableMapOf<String, User>() // criação do mapa de utilizadores, inicialmente vazio
    this.forEach { user -> // por cada utilizador da lista
        val existingUser = userMap[user.login] // // tenta encontrar o utilizador no mapa usando o login como chave
        if (existingUser != null){ // caso exista
            val totalContributions = existingUser.contributions + user.contributions
            // soma-se as contribuições que o utilizador já tinha acumulado no mapa com as contribuições atualmente
            // a serem processadas | exemplo: se o utilizador já tiver 10 contribuições e de momento estiverem a ser
            // processadas 2, a soma será 10 + 2 = 12
            userMap[user.login] = User(user.login, totalContributions) // atualiza o mapa de utilizadores com a nova soma
        } else userMap[user.login] = user // caso não exista (é a primeira vez que encontramos este utilizador),
                                          // é adicionado diretamente ao mapa com os seus valores originais
    }
    // extrai apenas os valores do mapa (a lista final de objetos User, ignorando as chaves/logins) e
    // devolve essa lista ordenada de forma descendente (do maior número de contribuições para o menor)
    return userMap.values.sortedByDescending { it.contributions }
}