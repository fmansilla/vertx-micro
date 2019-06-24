package ar.ferman.vertxmicro.ranking

class InMemoryUserRankingRepository : UserRankingRepository {
    private val data = mutableMapOf<String, UserRanking>()

    override fun find(userId: String): UserRanking? = data[userId]

    override fun findAll(): List<UserRanking> = data.values.toList()

    override fun put(userRanking: UserRanking) {
        data[userRanking.userId] = userRanking
    }
}