package ar.ferman.vertxmicro.ranking.rest

import ar.ferman.vertxmicro.ranking.rest.handler.SuspendingHandler
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory


private val logger = LoggerFactory.getLogger(RankingResources.javaClass)

/**
 * An extension method for simplifying coroutines usage with Vert.x Web routers
 */
fun Route.coroutineHandler(coroutineScope: CoroutineScope, fn: suspend (RoutingContext) -> Unit) {
    logger.info("Registering handler for: $path")
    handler { ctx ->
        coroutineScope.launch(ctx.vertx().dispatcher()) {
            try {
                fn(ctx)
            } catch (e: Exception) {
                ctx.fail(e)
            }
        }
    }
}

fun Route.coroutineHandler(coroutineScope: CoroutineScope, suspendingHandler: SuspendingHandler) {
    coroutineHandler(coroutineScope) { suspendingHandler.invoke(it) }
}