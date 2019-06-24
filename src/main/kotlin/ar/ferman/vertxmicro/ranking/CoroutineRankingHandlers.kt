package ar.ferman.vertxmicro.ranking

import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import javafx.application.Application.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object CoroutineRankingHandlers {

    fun registerOn(router: Router, coroutineScope: CoroutineScope) = router.run {
        get("/rankings").coroutineHandler(coroutineScope) { handleFindAllRankings(it) }
        get("/rankings/:id").coroutineHandler(coroutineScope) { handleFindRanking(it) }
        post("/rankings/:id").coroutineHandler(coroutineScope) { handlePublishRanking(it) }
    }

    // Handlers
    private fun handleFindRanking(context: RoutingContext) {
        val userRanking = UserRanking("ferman", 5)

        context.response().endWithJson(userRanking.toJsonRepresentation())
    }

    private fun handleFindAllRankings(context: RoutingContext) {
        val userRankings = listOf(UserRanking("ferman", 5))

        context.response().endWithJson(userRankings.map { it.toJsonRepresentation() })
    }

    private suspend fun handlePublishRanking(context: RoutingContext) {
//        waitForPostData(context)
delay(1000)
        val userRanking = Json.decodeValue(context.body, UserRankingJson::class.java).toUserRanking()
        println("Published: $userRanking")

        context.response().setStatusCode(200).end()
    }

    private suspend fun waitForPostData(context: RoutingContext) : Unit = suspendCoroutine{ continuation ->
        context.addBodyEndHandler {
            continuation.resume(Unit)
        }
    }

    /**
     * An extension method for simplifying coroutines usage with Vert.x Web routers
     */
    private fun Route.coroutineHandler(coroutineScope: CoroutineScope, fn: suspend (RoutingContext) -> Unit) {
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
}

/**
 * Extension to the HTTP response to output JSON objects.
 */
private fun HttpServerResponse.endWithJson(obj: Any) {
    this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
}