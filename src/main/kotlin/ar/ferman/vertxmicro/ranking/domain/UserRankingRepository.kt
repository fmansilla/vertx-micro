package ar.ferman.vertxmicro.ranking.domain

interface UserRankingRepository {

    fun find(userId: String): UserRanking?

    fun findAll(): List<UserRanking>

    fun put(userRanking: UserRanking)
}