package ar.ferman.vertxmicro.ranking.action

import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository

interface PublishScore {
    suspend operator fun invoke(userId: String, score: Int)
}

class DefaultPublishScore(private val userRankingRepository: UserRankingRepository) : PublishScore {
    override suspend operator fun invoke(userId: String, score: Int) {
        if (shouldPutUserRanking(userId, score)) {
            userRankingRepository.put(UserRanking(userId, score))
        }
    }

    private fun shouldPutUserRanking(userId: String, score: Int): Boolean {
        return userRankingRepository.find(userId)?.let { current -> score > current.score } != false
    }
}