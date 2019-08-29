package ar.ferman.vertxmicro.ranking.action

import ar.ferman.vertxmicro.ranking.domain.TopRankingRepository
import ar.ferman.vertxmicro.ranking.domain.UserRanking

class FindTopUserRankings(private val repository: TopRankingRepository) {
    suspend operator fun invoke(amount: Int): List<UserRanking> {
        return repository.get().sortedByDescending { it.score }.take(amount)
    }
}

