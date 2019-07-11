package ar.ferman.vertxmicro.ranking.domain


interface UserRankingRepository {
    fun findBy(userId: UserId): UserRanking?
}