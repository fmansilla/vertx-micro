package ar.ferman.vertxmicro.ranking.action

import ar.ferman.vertxmicro.ranking.domain.TopRankingRepository
import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository

class PublishScore(
    private val userRankingRepository: UserRankingRepository,
    private val topRankingRepository: TopRankingRepository
) {
    suspend operator fun invoke(userId: String, score: Int) {
        if (isNewHighScore(userId, score)) {
            val userRanking = UserRanking(userId, score)
            userRankingRepository.put(userRanking)
            updateTopUserRankingsIfProper(userRanking)
        }
    }

    private suspend fun isNewHighScore(userId: String, score: Int): Boolean {
        return userRankingRepository.find(userId)?.let { current -> score > current.score } != false
    }

    private suspend fun updateTopUserRankingsIfProper(userRanking: UserRanking) {
        if (topRankingRepository.isNewTopHighScore(userRanking)) {
            topRankingRepository.put(userRanking)
        }
    }
}