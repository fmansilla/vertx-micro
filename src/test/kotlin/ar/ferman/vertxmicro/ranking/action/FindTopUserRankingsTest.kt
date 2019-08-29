package ar.ferman.vertxmicro.ranking.action

import ar.ferman.vertxmicro.ranking.domain.TopRankingRepository
import ar.ferman.vertxmicro.ranking.domain.UserRanking
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class FindTopUserRankingsTest {

    @Test
    fun `find top rankings when no one available returns empty`() = runBlocking {
        val repository = mock<TopRankingRepository> {
            onBlocking() { get() } doReturn listOf()
        }
        val findTopUserRankings = FindTopUserRankings(repository)
        val topUserRankings = findTopUserRankings(10)
        then(topUserRankings).isEmpty()
    }

    @Test
    fun `find top N rankings when more are available`() = runBlocking<Unit> {
        val repository = mock<TopRankingRepository> {
            onBlocking { get() } doReturn listOf(
                UserRanking("d", 4),
                UserRanking("b", 8),
                UserRanking("a", 10),
                UserRanking("c", 6)
            )
        }
        val findTopUserRankings = FindTopUserRankings(repository)
        val topUserRankings = findTopUserRankings(2)

        then(topUserRankings)
            .containsExactly(
                UserRanking("a", 10),
                UserRanking("b", 8)
            )
    }

    @Test
    fun `find top N rankings when less are available`() = runBlocking<Unit> {
        val repository = mock<TopRankingRepository> {
            onBlocking { get() } doReturn listOf(
                UserRanking("b", 8),
                UserRanking("a", 10)
            )
        }
        val findTopUserRankings = FindTopUserRankings(repository)
        val topUserRankings = findTopUserRankings(10)

        then(topUserRankings).containsExactly(
            UserRanking("a", 10),
            UserRanking("b", 8)
        )
    }
}
