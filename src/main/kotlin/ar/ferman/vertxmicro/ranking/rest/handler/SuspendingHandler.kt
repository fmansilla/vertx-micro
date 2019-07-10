package ar.ferman.vertxmicro.ranking.rest.handler

import io.vertx.ext.web.RoutingContext

interface SuspendingHandler {
    suspend operator fun invoke(context: RoutingContext)
}