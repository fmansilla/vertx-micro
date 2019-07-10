package ar.ferman.vertxmicro.ranking.action

import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository

interface FindUserRanking {
    suspend operator fun invoke(userId: String): UserRanking

    class UserRankingNotFoundException : RuntimeException()
}

class DefaultFindUserRanking(private val userRankingRepository: UserRankingRepository) : FindUserRanking {
    override suspend operator fun invoke(userId: String): UserRanking {
        return userRankingRepository.find(userId) ?: throw FindUserRanking.UserRankingNotFoundException()
    }
}
