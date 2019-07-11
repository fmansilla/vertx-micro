package ar.ferman.vertxmicro.ranking.action

import ar.ferman.vertxmicro.ranking.action.GetUserRanking.UserRankingNotFound
import ar.ferman.vertxmicro.ranking.domain.UserId
import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test


class GetUserRankingTest {
    private var throwable: Throwable? = null
    private lateinit var userRanking: UserRanking
    private lateinit var repository: UserRankingRepository
    private lateinit var getUserRanking: GetUserRanking

    @Test
    internal fun `throw UserRankingNotFound when user ranking does not exist`() {
        givenEmptyUserRankingRepository()
        this.getUserRanking = GetUserRanking(repository)

        whenGetUserRanking("charly-gato")

        then(throwable).isInstanceOf(UserRankingNotFound::class.java)
    }

    @Test
    internal fun `get user ranking by userId`() {
        givenUserRankingRepositoryWith(UserRanking("charly-gato", 10))
        this.getUserRanking = GetUserRanking(repository)

        whenGetUserRanking("charly-gato")

        then(userRanking).isEqualTo(UserRanking("charly-gato", 10))
    }

    private fun givenUserRankingRepositoryWith(userRanking: UserRanking) {
        this.repository = mock {
            on { findBy(any()) } doReturn userRanking
        }
    }

    private fun givenEmptyUserRankingRepository() {
        this.repository = mock {
            on { findBy(any()) } doReturn null
        }
    }

    private fun whenGetUserRanking(userId: UserId) {
        this.throwable = catchThrowable {
            this.userRanking = getUserRanking(userId)
        }
    }
}
