package ar.ferman.vertxmicro.ranking.rest

import ar.ferman.vertxmicro.ranking.domain.UserRanking
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class UserRankingJson @JsonCreator constructor(
    @JsonProperty("userId") val userId: String,
    @JsonProperty("score") val score: Int
) {
    fun toUserRanking(): UserRanking =
        UserRanking(userId, score)
}

fun UserRanking.toJsonRepresentation(): UserRankingJson {
    return UserRankingJson(userId, score)
}