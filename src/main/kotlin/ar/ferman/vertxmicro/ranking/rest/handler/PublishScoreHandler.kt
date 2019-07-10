package ar.ferman.vertxmicro.ranking.rest.handler

import ar.ferman.vertxmicro.ranking.action.PublishScore
import ar.ferman.vertxmicro.ranking.rest.UserRankingJson
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext

class PublishScoreHandler(val publishScore: PublishScore) : SuspendingHandler {

    override suspend fun invoke(context: RoutingContext) = context.run {
        val userRanking = Json.decodeValue(context.body, UserRankingJson::class.java)
        println("Published: $userRanking")
        publishScore(userRanking.userId, userRanking.score)

        response().end()
    }
}