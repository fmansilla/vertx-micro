package ar.ferman.vertxmicro.ranking.rest.handler

import ar.ferman.vertxmicro.ranking.action.FindUserRanking
import ar.ferman.vertxmicro.ranking.action.FindUserRanking.UserRankingNotFoundException
import ar.ferman.vertxmicro.ranking.rest.endWithJson
import ar.ferman.vertxmicro.ranking.rest.toJsonRepresentation
import io.vertx.ext.web.RoutingContext

class FindUserRankingHandler(val findUserRanking: FindUserRanking) : SuspendingHandler {

    override suspend fun invoke(context: RoutingContext) = context.run {
        try {
            response().endWithJson(findUserRanking(pathParam("id")).toJsonRepresentation())
        } catch (e: UserRankingNotFoundException) {
            response().setStatusCode(404).end()
        }
    }
}