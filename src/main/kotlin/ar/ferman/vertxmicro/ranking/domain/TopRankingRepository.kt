package ar.ferman.vertxmicro.ranking.domain

interface TopRankingRepository {
    suspend fun get(): List<UserRanking>
    suspend fun put(topUserRanking: UserRanking)
    suspend fun isNewTopHighScore(topUserRanking: UserRanking): Boolean
}