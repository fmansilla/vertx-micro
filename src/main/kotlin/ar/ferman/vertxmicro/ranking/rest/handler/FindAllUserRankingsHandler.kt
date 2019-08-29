package ar.ferman.vertxmicro.ranking.rest.handler

import ar.ferman.vertxmicro.ranking.action.FindTopUserRankings
import ar.ferman.vertxmicro.ranking.rest.endWithJson
import ar.ferman.vertxmicro.ranking.rest.toJsonRepresentation
import io.vertx.ext.web.RoutingContext

class FindTopUserRankingsHandler(val findTopUserRankings: FindTopUserRankings) : SuspendingHandler {
    companion object {
        const val TOP_RANKING_SIZE = 10
    }

    override suspend fun invoke(context: RoutingContext) = context.run {
        response().endWithJson(findTopUserRankings(TOP_RANKING_SIZE).map { it.toJsonRepresentation() })
    }
}