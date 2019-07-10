package ar.ferman.vertxmicro.ranking.action

import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository
import ar.ferman.vertxmicro.utils.logger

interface FindAllUserRankings {
    suspend operator fun invoke(): List<UserRanking>
}

class DefaultFindAllUserRankings(private val userRankingRepository: UserRankingRepository) : FindAllUserRankings {
    override suspend fun invoke(): List<UserRanking> {
        return userRankingRepository.findAll().also {
            logger.debug("${it.size} user rankings found")
        }
    }

    companion object {
        val logger = logger()
    }
}