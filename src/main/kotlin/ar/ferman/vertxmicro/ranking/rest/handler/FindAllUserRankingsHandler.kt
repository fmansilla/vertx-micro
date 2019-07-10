package ar.ferman.vertxmicro.ranking.rest.handler

import ar.ferman.vertxmicro.ranking.action.FindAllUserRankings
import ar.ferman.vertxmicro.ranking.rest.endWithJson
import ar.ferman.vertxmicro.ranking.rest.toJsonRepresentation
import io.vertx.ext.web.RoutingContext

class FindAllUserRankingsHandler(val findAllUserRankings: FindAllUserRankings) : SuspendingHandler {

    override suspend fun invoke(context: RoutingContext) = context.run {
        response().endWithJson(findAllUserRankings().map { it.toJsonRepresentation() })
    }
}