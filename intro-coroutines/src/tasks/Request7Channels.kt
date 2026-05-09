package tasks

import contributors.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun loadContributorsChannels(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    var counter = 0
    var allUsers = listOf<User>()
    var isLastRepo = false
    coroutineScope {
        val channel = Channel<List<User>>()
        for (repo in repos) {
            launch {
                val users = service
                    .getRepoContributors(req.org, repo.name)
                    .also { logUsers(repo, it) }
                    .bodyList()
                channel.send(users)
                counter ++
            }
        }
        repeat (repos.size) {
            val users = channel.receive()
            allUsers = (allUsers + users).aggregate()
            if (counter == repos.size) isLastRepo = true
            updateResults(allUsers, isLastRepo)
        }
    }
}
