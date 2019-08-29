package ar.ferman.vertxmicro.ranking.action

import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class FindUserRankingTest {

    private lateinit var userRankingRepository: UserRankingRepository
    private lateinit var findUserRanking: FindUserRanking

    private companion object {
        const val USER_ID = "ferman"
    }

    @Test
    fun `find existing user ranking`() = runBlocking {
        userRankingRepository = mock()
        findUserRanking = FindUserRanking(userRankingRepository)

        findUserRanking(USER_ID)

        verify(userRankingRepository).find(USER_ID)
    }
}
