package ar.ferman.vertxmicro.ranking.domain

interface SyncTopRankingRepository {
    fun get(): List<UserRanking>
    fun put(topUserRankings: List<UserRanking>)
}