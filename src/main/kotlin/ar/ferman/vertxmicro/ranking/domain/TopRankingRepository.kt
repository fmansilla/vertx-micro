package ar.ferman.vertxmicro.ranking.domain

interface TopRankingRepository {
    suspend fun get(): List<UserRanking>
    suspend fun put(topUserRankings: List<UserRanking>)
}