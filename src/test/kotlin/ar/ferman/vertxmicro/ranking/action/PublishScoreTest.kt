package ar.ferman.vertxmicro.ranking.action

import ar.ferman.vertxmicro.ranking.domain.TopRankingRepository
import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class PublishScoreTest {

    private lateinit var userRankingRepository: UserRankingRepository
    private lateinit var topRankingRepository: TopRankingRepository
    private lateinit var publishScore: PublishScore

    private companion object {
        const val USER_ID = "ferman"
        const val USER_ID_2 = "albert"
        const val USER_ID_3 = "jason"
        val EMPTY: UserRanking? = null
    }

    @Test
    fun `first published score is a new high score`() = runBlocking {
        givenCurrentUserHighScore(EMPTY)
        givenTopHighScores()
        givenPublishScoreAction(maxHighScoresAmount = 1)

        publishScore(USER_ID, score = 2)

        verify(userRankingRepository).put(eq(UserRanking(USER_ID, score = 2)))
    }

    @Test
    fun `publishing a new score lower than high score is ignored`() = runBlocking {
        givenCurrentUserHighScore(UserRanking(USER_ID, score = 2))
        givenTopHighScores()
        givenPublishScoreAction(maxHighScoresAmount = 1)

        publishScore(USER_ID, score = 1)

        verify(userRankingRepository, never()).put(eq(UserRanking(USER_ID, score = 1)))
    }

    @Test
    fun `publishing a new score greater than own high score is saved`() = runBlocking {
        givenCurrentUserHighScore(UserRanking(USER_ID, score = 2))
        givenTopHighScores()
        givenPublishScoreAction(maxHighScoresAmount = 1)

        publishScore(USER_ID, score = 10)

        verify(userRankingRepository).put(eq(UserRanking(USER_ID, score = 10)))
    }

    @Test
    fun `publishing a new score greater than own high score and some top high scores is saved in top scores`() =
        runBlocking {
            givenCurrentUserHighScore(EMPTY)
            givenTopHighScores(UserRanking(USER_ID_2, 1), UserRanking(USER_ID_3, 20))
            givenPublishScoreAction(maxHighScoresAmount = 1)

            publishScore(USER_ID, 10)

            verify(topRankingRepository).put(eq(listOf(UserRanking(USER_ID, 10))))
        }

    @Test
    fun `publishing a new score greater than own high score but lower than all top high scores is not saved in top scores`() =
        runBlocking {
            givenCurrentUserHighScore(EMPTY)
            givenTopHighScores(UserRanking(USER_ID_2, 10), UserRanking(USER_ID_3, 20))
            givenPublishScoreAction(maxHighScoresAmount = 1)

            publishScore(USER_ID, 1)

            verify(topRankingRepository, never()).put(any())
        }

    @Test
    fun `publishing a new score greater than own high score but lower than all top high scores is saved in top scores when less than max amount`() =
        runBlocking {
            givenCurrentUserHighScore(EMPTY)
            givenTopHighScores(UserRanking(USER_ID_2, 10), UserRanking(USER_ID_3, 20))
            givenPublishScoreAction(maxHighScoresAmount = 2)

            publishScore(USER_ID, 1)

            verify(topRankingRepository, never()).put(any())
        }

    private suspend fun givenTopHighScores(vararg userRankings: UserRanking) {
        topRankingRepository = mock {
            onBlocking { get() } doReturn userRankings.toList()
        }
    }

    private fun givenPublishScoreAction(maxHighScoresAmount: Int) {
        publishScore = PublishScore(userRankingRepository, topRankingRepository, maxHighScoresAmount)
    }

    private suspend fun givenCurrentUserHighScore(highScore: UserRanking?) {
        userRankingRepository = mock {
            onBlocking { find(USER_ID) } doReturn highScore
        }
    }
}