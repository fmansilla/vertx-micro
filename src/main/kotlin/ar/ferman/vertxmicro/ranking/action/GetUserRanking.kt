package ar.ferman.vertxmicro.ranking.action

import ar.ferman.vertxmicro.ranking.domain.UserId
import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository


class GetUserRanking(private val repository: UserRankingRepository) {

    operator fun invoke(userId: UserId): UserRanking {
        return repository.findBy(userId) ?: throw UserRankingNotFound(userId)
    }


    class UserRankingNotFound(userId: UserId): RuntimeException("User $userId was not found")
}