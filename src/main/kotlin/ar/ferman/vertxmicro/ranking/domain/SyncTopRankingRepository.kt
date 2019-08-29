package ar.ferman.vertxmicro.ranking.domain

interface SyncTopRankingRepository {
    fun get(): List<UserRanking>
    fun put(topUserRanking: UserRanking)
    fun isNewTopHighScore(topUserRanking: UserRanking): Boolean
}