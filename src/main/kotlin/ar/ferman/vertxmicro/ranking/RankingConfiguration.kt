package ar.ferman.vertxmicro.ranking

import ar.ferman.vertxmicro.ranking.action.*
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository
import ar.ferman.vertxmicro.ranking.infrastructure.InMemoryUserRankingRepository

object RankingConfiguration {

    private val userRankingRepository: UserRankingRepository = InMemoryUserRankingRepository()

    object Actions {

        val FindAllUserRankings: FindAllUserRankings = DefaultFindAllUserRankings(userRankingRepository)
        val FindUserRanking: FindUserRanking = DefaultFindUserRanking(userRankingRepository)
        val PublishScore: PublishScore = DefaultPublishScore(userRankingRepository)
    }
}