package tasks

import contributors.*

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    var usersList = listOf<User>()
    var isLastRepo = false
    var counter = 0
    for (repo in repos) {
        val users = service
            .getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()
        usersList = (usersList + users).aggregate()
        counter ++
        if (counter == repos.size) isLastRepo = true
        updateResults(usersList, isLastRepo)
    }
}
