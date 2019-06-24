package ar.ferman.vertxmicro.ranking

import io.vertx.core.Handler
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

object RankingHandlers {



    fun registerOn(router: Router) {
        router.apply {
            get("/rankings").handler(handlerFindAllRankings)
            get("/rankings/:id").handler(handlerFindRanking)
            post("/rankings/:id").handler(handlerPublishRanking)
        }
    }

    // Handlers
    private val handlerFindRanking = Handler<RoutingContext> { req ->
        val userRanking = UserRanking("ferman", 5)

        req.response().endWithJson(userRanking.toJsonRepresentation())
    }

    private val handlerFindAllRankings = Handler<RoutingContext> { req ->
        val userRankings = listOf(UserRanking("ferman", 5))

        req.response().endWithJson(userRankings.map { it.toJsonRepresentation() })
    }

    private val handlerPublishRanking = Handler<RoutingContext> { req ->
        val userRanking = Json.decodeValue(req.body, UserRankingJson::class.java).toUserRanking()
        println("Published: $userRanking")

        req.response().setStatusCode(200).end()
    }
}

/**
 * Extension to the HTTP response to output JSON objects.
 */
private fun HttpServerResponse.endWithJson(obj: Any) {
    this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
}