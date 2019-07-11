package ar.ferman.vertxmicro.ranking.rest

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class UserRankingJson @JsonCreator constructor(
    @JsonProperty("userId") val userId: String,
    @JsonProperty("score") val score: Int
)