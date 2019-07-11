package ar.ferman.vertxmicro.ranking.infrastructure

import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository
import ar.ferman.vertxmicro.ranking.domain.UserRanking

class InMemoryUserRankingRepository : UserRankingRepository {
    private val data = mutableMapOf<String, UserRanking>().also {
        it["ferman"] = UserRanking("ferman", 5)
    }

    override suspend fun find(userId: String): UserRanking? = data[userId]

    override suspend fun findAll(): List<UserRanking> = data.values.toList()

    override suspend fun put(userRanking: UserRanking) {
        data[userRanking.userId] = userRanking
    }
}