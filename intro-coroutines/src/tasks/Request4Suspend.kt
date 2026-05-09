package tasks

import contributors.*

suspend fun loadContributorsSuspend(service: GitHubService, req: RequestData): List<User> {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    return repos.flatMap { repo ->
        service
            .getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()
    }.aggregate()
}

/*
A diferença principal é que as novas funções já devolvem a resposta diretamente, sem ser preciso chamar .execute().

Segundo a interface GitHubService, getOrgRepos e getRepoContributors retornam uma Response
 */