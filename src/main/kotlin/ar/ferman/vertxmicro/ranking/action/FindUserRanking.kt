package ar.ferman.vertxmicro.ranking.action

import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository


class FindUserRanking(private val userRankingRepository: UserRankingRepository) {

    suspend operator fun invoke(userId: String): UserRanking {
        return userRankingRepository.find(userId) ?: throw UserRankingNotFoundException()
    }

    class UserRankingNotFoundException : RuntimeException()
}
