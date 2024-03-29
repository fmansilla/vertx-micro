package ar.ferman.vertxmicro.ranking.domain

interface UserRankingRepository {
    suspend fun find(userId: String): UserRanking?
    suspend fun put(userRanking: UserRanking)
}