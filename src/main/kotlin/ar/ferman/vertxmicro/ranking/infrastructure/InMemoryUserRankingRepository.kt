package ar.ferman.vertxmicro.ranking.infrastructure

import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository

class InMemoryUserRankingRepository : UserRankingRepository {
    private val data = mutableMapOf<String, UserRanking>()

    override suspend fun find(userId: String): UserRanking? = data[userId]

    override suspend fun put(userRanking: UserRanking) {
        data[userRanking.userId] = userRanking
    }
}